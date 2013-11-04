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

#>>> bm.verts.layers.int.new('ax')

bl_info = {
    "name": "Axis Cross Section",
    "author": "twitter.com/jplur_",
    "version": (1, 0),
    "blender": (2, 56, 0),
    "location": "3d View > Tools",
    "description": "",
    "wiki_url": "",
    "tracker_url": "",
    "category": "Render"}

import bpy, bmesh, math
from bpy.props import BoolProperty
from mathutils import *

from bpy_extras.io_utils import axis_conversion


def clear_selected(context):
    set_object_mode(context)
    for o in context.selected_objects:
        o.select = False

def set_object_mode(context):
    if bpy.ops.object.mode_set.poll(): 
        bpy.ops.object.mode_set(mode='OBJECT') 
    else: 
        print("mode_set() context is incorrect, current mode is", bpy.context.mode)

def set_edit_mode(context):
    if bpy.ops.object.mode_set.poll(): 
        bpy.ops.object.mode_set(mode='EDIT') 
    else: 
        print("mode_set() context is incorrect, current mode is", bpy.context.mode)

def clear_select(context):
    set_edit_mode(context)
    context.tool_settings.mesh_select_mode = (True, True, True)
    bpy.ops.mesh.select_all(action="DESELECT")

def select_int_layer(context, name):
    obj = context.active_object
    set_edit_mode(context)
    bm = bmesh.from_edit_mesh(obj.data)
    plus = bm.verts.layers.int.get(name)
    print(plus)
    print(bm.verts.layers.int.keys())
    result = []
    for v in bm.verts:
        if v[plus] == 1:

            v.select_set(True)

    set_object_mode(context)

    
    
    


def do_magic(self, context):
    

    # clear_selected(context)
    # set_object_mode(context)

    target_list = []
    for o in context.selected_objects:
        if o.type == "MESH":
            clear_selected(context)
            context.scene.objects.active = o
            bpy.ops.object.duplicate()
            new = context.active_object
            new.name = "cross-section"
            target_list.append(new)
    print(target_list)
    for o in target_list:
            context.scene.objects.active = o
            context.tool_settings.mesh_select_mode = (False, True, False)
            saved_mesh = o.data.copy()
            
            #o.data.transform(o.matrix_local)

            set_edit_mode(context)
            bpy.ops.mesh.select_all(action="DESELECT")
            set_object_mode(context)

            bm = bmesh.new()
            bm.from_mesh(o.data)

            plus = bm.verts.layers.int.new('+')
            minus = bm.verts.layers.int.new('-')

            target_edges = []
            valuable = []
            for edge in bm.edges:
                edge.select_set(False)
                v1 = edge.verts[0]
                v2 = edge.verts[1]
                if v1.co[1] <= 0 and v2.co[1] > 0:
                    v1[minus] = 1
                    v2[plus] = 1
                    target_edges.append(edge)
                    edge.select_set(True)
                    valuable.append(v1.index)
                    valuable.append(v2.index)

                elif v2.co[1] <= 0 and v1.co[1] > 0:
                    v2[minus] = 1
                    v1[plus] = 1
                    target_edges.append(edge)
                    edge.select_set(True)
                    valuable.append(v1.index)
                    valuable.append(v2.index)

            

            

            bm.to_mesh(o.data)

            #bm.free()
            #clear_select(context)
            #context.tool_settings.mesh_select_mode = (True, False, False)
            #select_int_layer(context, "+")
            #select_int_layer(context, "-")
            #return
            if len(target_edges) > 0:

                context.tool_settings.mesh_select_mode = (False, True, False)
                set_edit_mode(context)
                context.tool_settings.mesh_select_mode = (False, True, False)

                bpy.ops.mesh.duplicate()
                bpy.ops.mesh.select_all(action="INVERT")
                bpy.ops.mesh.delete(type="EDGE")
                bpy.ops.mesh.select_all()
                bpy.ops.mesh.subdivide()

                #context.tool_settings.mesh_select_mode = (True, False, False)
                #bpy.ops.mesh.select_all(action="INVERT")
                #bpy.ops.mesh.delete(type="VERT")

                context.tool_settings.mesh_select_mode = (True, False, False)
                set_object_mode(context)
                
                bm = bmesh.new()
                bm.from_mesh(o.data)

                for f in bm.faces:
                    f.select_set(False)
                for e in bm.edges:
                    e.select_set(False)
                for v in bm.verts:
                    v.select_set(False)

                #select_int_layer(context, "+")
                #select_int_layer(context, "-")
                
                
                bm.to_mesh(o.data)

                set_edit_mode(context)



                #bpy.ops.mesh.select_non_manifold()
                #bpy.ops.mesh.select_all(action="INVERT")

                

                

                
                 
                context.tool_settings.mesh_select_mode = (True, False, False)
                set_object_mode(context)
                bm = bmesh.new()
                bm.from_mesh(o.data)
                #for v in bm.verts:
                #    if v.select:

                def is_median(v):
                    n = []
                    l = len(v.link_edges)
                    if l is 1:
                        return False
                    for e in v.link_edges:
                        for vv in e.verts:
                            if vv != v:
                                if v not in n:
                                    n.append(vv)
                    if n[0].co[1] < v.co[1] and n[1].co[1] > v.co[1]:
                        return True
                    if n[0].co[1] > v.co[1] and n[1].co[1] < v.co[1]:
                        return True
                    return False


                for v in bm.verts:
                    if is_median(v):
                        v.select_set(True)
                        print("found middle v")
                        a = False
                        b = False


                        for e in v.link_edges:
                            
                            for vv in e.verts:
                                if vv != v and not vv.select:
                                    if vv.co[1] <= 0:
                                        a = vv
                                    else:
                                        b = vv

                        if a and b:
                            print("found A and B")
                            l = abs(a.co[1]) + abs(b.co[1])
                            r = abs(a.co[1])/l
                            dv = a.co - b.co
                            v.co = a.co - dv*r #(dv*l)
                            #v.co = (a.co + b.co) * .5
                            v.select_set(True)
                            a.select_set(False)
                            b.select_set(False)
                                


                context.tool_settings.mesh_select_mode = (True, False, False)
                set_object_mode(context)
                bm.to_mesh(o.data)

                set_edit_mode(context)
                bpy.ops.mesh.select_all(action="INVERT")
                bpy.ops.mesh.delete(type="VERT")
                set_object_mode(context)

                #o.data.transform(o.matrix_local.inverted())



    
    return 'FINISHED'


class AxisCrossection(bpy.types.Operator):
    bl_idname      = 'axis_cross.run'
    bl_label       = "axis_cross"
    bl_description = "Automates knife_project for all objects in the current layer, along the selected axis."

    def invoke(self, context, event):
        do_magic(self, context)
        return{'FINISHED'}

class VIEW3D_PT_axis_crossection(bpy.types.Panel ):

    bl_label = "Mesh Tools"
    bl_space_type = 'VIEW_3D'
    bl_region_type = 'TOOLS'

    

    def draw(self, context):
        layout = self.layout
        scene = context.scene

        row = layout.row()
        row.label("Cross Section by Axis:")
        row.operator('axis_cross.run', text="Make")




def register():
    bpy.utils.register_module(__name__)


def unregister():
    bpy.utils.unregister_module(__name__)


if __name__ == "__main__":
    register()













#bpy.ops.mesh.knife_project()

    
    #bpy.ops.mesh.primitive_plane_add(radius=100, location=(0,0,0), rotation=(math.pi/2,0,0))

    #guide = context.object
    #guide.name = "__temp_axis_cross_guide"


    #set_object_mode(context)


    #guide.select = False
    #print("?: "+context.active_object.name)
    # for ob in target_list:
 
            
    #         bpy.context.scene.objects.active = ob
    #         ob.select = True
    #         bpy.ops.object.duplicate()
    #         ob.select = False
    #         obj = bpy.context.scene.objects.active
    #         #guide.select = True
    #         obj.select = True
    #         set_edit_mode(context)

            
            #bpy.ops.mesh.knife_project()
            #bpy.ops.mesh.knife_tool(context, "INVOKE_DEFAULT")
            #return 'FINISHED'
            # set_object_mode(context)
            # obj.select = False
            # context.scene.objects.active = None

    #ob = target_list[0]
    #bpy.context.scene.objects.active = ob
    #ob.select = True
    #set_edit_mode(context)
    #bpy.ops.mesh.knife_tool(context)