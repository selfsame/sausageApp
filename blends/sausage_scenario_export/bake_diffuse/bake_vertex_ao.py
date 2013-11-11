# The py must be located in scripts\addons to show up in the AddOns list.
# The pyc and py can be in 25 folder once added.

bl_info = {
	"name": "3D View: Ambient Occlusion GL",
	"author": "Anthony D'Agostino",
	"version": (1, 0, 0),
	"blender": (2, 5, 8),
	"location": "Tool Shelf",
	"description": "Generate Ambient-Occlusion via Open GL",
	"warning": "", # used for warning icon and text in addons panel
	"wiki_url": "http://wiki.blender.org/index.php/Extensions:2.5/Py/Scripts/3D interaction/",
	"category": "3D View"}

import bpy, mathutils, time, pprint
from bgl import *

def get_viewport():
	buffer = Buffer(GL_INT, 4)
	glGetIntegerv(GL_VIEWPORT, buffer)
	x,y,w,h = buffer
	return x,y,w,h

def calc_ao_factor(buffer):
	data = buffer.to_list()
	samples = 0.75*len(data)
	misses = -0.25*len(data) + data.count(255)
	factor = misses/samples
	return factor

def read_frame():
	hemirez = 64  # must be power-of-two
	w,h = [hemirez,hemirez]
	buffer = Buffer(GL_BYTE, [w*h])
	glReadPixels(xorig,yorig, w,h, GL_LUMINANCE, GL_UNSIGNED_BYTE, buffer)
	return calc_ao_factor(buffer)

def get_ao_color(eye, lookat, displist):
	#glDrawBuffer(GL_AUX0)
	#glDrawBuffer(GL_FRONT)
	#glDrawBuffer(GL_BACK)
	glClear(GL_COLOR_BUFFER_BIT)
	render_hemicube(eye, lookat, displist)
	#glReadBuffer(GL_AUX0)
	#glReadBuffer(GL_FRONT)
	#glReadBuffer(GL_BACK)
	factor = read_frame()
	color = [factor]*3
	return color

def make_disp_list(mesh):
	displist = glGenLists(1)
	glNewList(displist, GL_COMPILE)
	for face in mesh.polygons:
		glBegin(GL_POLYGON)
		for i in face.vertices:
			(x,y,z) = mesh.vertices[i].co
			glVertex3f(x, y, z)
		glEnd()
	glEndList()
	return displist

def setup_gl():
	glClearColor(1,1,1,0)	# Sky Color
	glColor4f(0,0,0,1)		# Obj Color
	glShadeModel(GL_FLAT)
	glDisable(GL_LIGHTING)

def get_up_right(lookat):
	(x,y,z) = lookat
	if abs(y) > abs(x) or abs(y) > abs(z):
		up = mathutils.Vector([1,0,0])
	else:
		up = mathutils.Vector([0,1,0])
	right = mathutils.Vector.cross(up, lookat)
	up = mathutils.Vector.cross(lookat, right)
	return (up,right)

def render_hemicube(eye, lookat, displist):
	hemirez = 64  # must be power-of-two
	(up,right) = get_up_right(lookat)
	p1 = int(hemirez * 0.00)
	p2 = int(hemirez * 0.25)
	p3 = int(hemirez * 0.50)
	p4 = int(hemirez * 0.75)
	[w,h] = [p3,p2]
	render_view(eye, eye+lookat, up, (-1,1,-1,1), (p2,p2,w,w), displist) # Center
	render_view(eye, eye+right,  up, ( 0,1,-1,1), (p1,p2,h,w), displist) # Right
	render_view(eye, eye-right,  up, (-1,0,-1,1), (p4,p2,h,w), displist) # Left
	render_view(eye, eye-up, lookat, (-1,1, 0,1), (p2,p1,w,h), displist) # Down
	render_view(eye, eye+up,-lookat, (-1,1,-1,0), (p2,p4,w,h), displist) # Up

def render_view(eye, lookat, up, frustum, viewport, displist):
	near = 0.01
	far = 100.0
	ex,ey,ez = eye
	dx,dy,dz = lookat
	ux,uy,uz = up
	l,r,b,t = [i*near for i in frustum]
	x,y,w,h = viewport
	glMatrixMode(GL_PROJECTION)
	glLoadIdentity()
	glFrustum(l,r, b,t, near,far)
	glMatrixMode(GL_MODELVIEW)
	glLoadIdentity()
	gluLookAt(ex,ey,ez, dx,dy,dz, ux,uy,uz)
	glViewport(x+xorig, y+yorig, w, h)
	glCallList(displist)

def calc_ao_colors(mesh, displist):
	vps = {}
	for face in mesh.polygons:
		for i in face.vertices:
			eye = mesh.vertices[i].co
			lookat = mesh.vertices[i].normal if face.use_smooth else face.normal
			key,val = map(tuple, [eye,lookat])
			vps.setdefault(key, {})[val]=None
	new = {}
	for key,vals in vps.items():
		new[key] = vals.keys()
	colors = {}
	index = 0
	numitems = max(len(new), 100)
	for key,vals in new.items():
		if not index % (numitems//50):
			percentage = index/numitems
			print("%2d%% completed." % (100*index/numitems), end="\r")
			#$glDrawBuffer(GL_BACK)
			#$Blender.Window.DrawProgressBar(percentage, "OpenGL AmbOcc")
		index += 1
		for val in vals:
			key2,val2 = map(mathutils.Vector, [key,val])
			colors[(key,val)] = get_ao_color(key2, val2, displist)
	#Blender.Window.DrawProgressBar(1.0, "done")
	return colors

def set_vc_layer(mesh, name):
	if name in mesh.vertex_colors: bpy.ops.mesh.vertex_color_remove()
	bpy.ops.mesh.vertex_color_add() #mesh.vertex_colors.new()
	mesh.vertex_colors.active.name = name

def add_ao_vcolors(mesh, displist):
	colors = calc_ao_colors(mesh, displist)
	#pprint.pprint(colors)
	#clear_vcolors(mesh)
	set_vcolors(mesh, colors)

def set_vcolors(mesh, colors):	# Only does 1/4?
	vcolors = mesh.vertex_colors.active.data
	for face in mesh.polygons:
		for i in face.vertices:
			col = vcolors[i]
			eye = mesh.vertices[i].co
			lookat = mesh.vertices[i].normal if face.use_smooth else face.normal
			key,val = map(tuple, [eye,lookat])
			col.color = colors[key,val]

def set_vcolors(mesh, colors):
	vcolors = mesh.vertex_colors.active.data
	for face in mesh.polygons:
		for i in face.loop_indices:
			col = vcolors[i]
			loop = mesh.loops[i]
			v = loop.vertex_index
			eye = mesh.vertices[v].co
			lookat = mesh.vertices[v].normal if face.use_smooth else face.normal
			key,val = map(tuple, [eye,lookat])
			col.color = colors[key,val]

def clear_vcolors(mesh):
	idx = mesh.vertex_colors.active_index
	vcolors = mesh.vertex_colors[idx].data
	for (i,face) in enumerate(mesh.polygons):
		col = vcolors[i]
		(col.color1, col.color2, col.color3, col.color4) = [[0.8,0.22,0.22]]*4

def main(context):
	bpy.ops.object.mode_set(mode="VERTEX_PAINT")
	object = context.object
	mesh = object.data
	object.show_x_ray = 1
	object.select = 1
	object.show_wire = 0
	#mesh.show_all_edges = 1
	context.space_data.viewport_shade = "TEXTURED"
	time1 = time.time()
	displist = make_disp_list(mesh)
	glPushAttrib(GL_ALL_ATTRIB_BITS)
	setup_gl()
	set_vc_layer(mesh, "AmbOcc GL")
	add_ao_vcolors(mesh, displist)
	glPopAttrib()
	time2 = time.time()
	bpy.ops.object.mode_set(mode="OBJECT")
	card = glGetString(GL_RENDERER)
	print("OpenGL AmbOcc time: %.1fs [%s]" % (time2-time1, card))

class AmbOccUi(bpy.types.Panel):
	bl_space_type = "VIEW_3D"
	bl_region_type = "TOOLS"
	bl_label = "AmbOcc Tools"
	bl_context = "objectmode"
	def draw(self, context):
		layout = self.layout
		obj = context.object
		col = layout.column()
		col.operator("screen.ambocc",text="Generate")

class AmbOccGL(bpy.types.Operator):
	"""Generate Ambient-Occlusion via Open GL"""
	bl_idname = "screen.ambocc"
	bl_label = "Ambient Occlusion GL"
	@classmethod
	def poll(self, context):
		return context.active_object != None
	def execute(self, context):
		main(context)
		return {"FINISHED"}

def register():
	bpy.utils.register_class(AmbOccGL)
	bpy.utils.register_class(AmbOccUi)

def unregister():
	bpy.utils.unregister_class(AmbOccGL)
	bpy.utils.unregister_class(AmbOccUi)

if __name__ == "__main__":
	register()

(xorig,yorig,_w,_h) = get_viewport()
