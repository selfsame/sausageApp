# ##### BEGIN GPL LICENSE BLOCK #####
#
#  This program is free software; you can redistribute it and/or
#  modify it under the terms of the GNU General Public License
#  as published by the Free Software Foundation; either version 2
#  of the License, or (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software Foundation,
#  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
#
# ##### END GPL LICENSE BLOCK ##### http://s3.selfsamegames.com.s3.amazonaws.com/users/jplur/published/sausage.html

# <pep8 compliant>

bl_info = {
    "name": "Vertex Diffuse Bake",
    "author": "twitter.com/jplur_",
    "version": (1, 0),
    "blender": (2, 56, 0),
    "location": "Render > Clay Render",
    "description": "",
    "wiki_url": "",
    "tracker_url": "",
    "category": "Render"}

import bpy, bmesh
from bpy.props import BoolProperty
from mathutils import *
from math import *
from bpy_extras.io_utils import axis_conversion


def do_magic(context):
    lights = []
    for o in context.scene.objects:
        if o.type == "LAMP":
            lights.append(o)
    print(lights)
    obj = context.active_object
    bm = bmesh.new()
    obm = bmesh.new() 
    o_mesh = obj.data
    mesh = obj.to_mesh(scene = bpy.context.scene, apply_modifiers = True, settings = 'PREVIEW')
    bm.from_mesh(mesh)
    obm.from_mesh(o_mesh)
    color = bm.loops.layers.color[0]
    ocolor = obm.loops.layers.color[0]
    obcolor = Vector(obj.color).xyz
    emit = 0
    if len(obj.data.materials) > 0:
        emit = obj.data.materials[0].emit
    find = 0
    for face in bm.faces:
        lind = 0
        for loop in face.loops:
            vcol = loop[color]
            vquat = obj.matrix_world.decompose()[1]
            v = loop.vert 
            n = v.normal * obj.matrix_world.inverted()
            n.normalize()
            co = (v.co * obj.matrix_world) + obj.location #* obj.matrix_world
            final = Vector((0,0,0)) 
            for lamp in lights:
                if lamp.data.type == "SUN":
                    energy = lamp.data.energy
                    lcolor = lamp.data.color
                    lv = lamp.matrix_world.to_quaternion() * Vector((0,0,1))
 


                    d = lv.dot(n) * energy
                    if d > 0:
                        final = final + ( (Vector(obcolor).xyz*d)/2 + (Vector(lcolor)*d)/2 )
                    #final = final + ( Vector((lcolor.r*obcolor[0],lcolor.g*obcolor[1],lcolor.b*obcolor[2]))  * d)
                else:
                    lv = lamp.location - co
                    distance = lv.length 
                    energy = lamp.data.energy*.1
                    lcolor = lamp.data.color

                    intensity = (lamp.data.distance*energy/(lamp.data.distance*energy + distance*distance))

                    d = n.dot(lv)
                    f = d*(intensity)
                    if d > 0:
                        final = final + ( Vector(lcolor) * f)/2 + ( obcolor * f )/2

            lc = Vector((vcol.r,vcol.b,vcol.g))  
            fc = final * lc

            final = (obcolor * emit) + (final * (1-emit))
            #loop[color] = (final[0], final[1], final[2])
            obm.faces[find].loops[lind][ocolor] = (final[0], final[1], final[2])
            lind += 1
        find += 1
    obm.to_mesh(o_mesh)

class BakeVertexDiffuse(bpy.types.Operator):
    bl_idname      = 'bake_vertex_diffuse.bake'
    bl_label       = "Add list item"
    bl_description = "Add list item"

    def invoke(self, context, event):
        do_magic(context)
        print("BakeVertexDiffuse: ")
        return{'FINISHED'}

class SCENE_PT_hello(bpy.types.Panel ):

    bl_label = "Bake Vertex Diffuse"
    bl_space_type = "PROPERTIES"
    bl_region_type = "WINDOW"
    bl_context = "render"

    def draw(self, context):
        layout = self.layout
        scene = context.scene

        row = layout.row()
        
        row.operator('bake_vertex_diffuse.bake', text="Bake Active")




def register():
    bpy.utils.register_module(__name__)


def unregister():
    bpy.utils.unregister_module(__name__)


if __name__ == "__main__":
    register()