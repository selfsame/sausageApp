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
# ##### END GPL LICENSE BLOCK #####

"""
Illuminato - Vertex-based lighting

Contact:        josip.kladaric@gmail.com
Collaborators:  CoDEmanX
  
All rights reserved.
"""

bl_info = {
    "name": "Illuminato",
    "description": "Bake lighting to Vertex Colors. ",
    "author": "Josip Kladaric",
    "version": (1, 0, 0),
    "blender": (2, 67, 0),
    "location": "Properties > Render",
    "warning": "Models with high poly counts can cause Blender to freeze for a while",
    "category": "Render"
    }

import bpy
from mathutils import Vector, Matrix, Color
from random import random
from time import time

bpy.types.Scene.illuminato_distance = bpy.props.FloatProperty(name='Distance',
                                                              min=0.01, max=50, default=10,
                                                              description='Distance the light can travel')

bpy.types.Scene.illuminato_localy = bpy.props.BoolProperty(name='Ignore others',default=False,
                                                              description='Ignore other objects while baking')

numofrays = [
    ("0", "Preview", "12 samples, use for quick preview purposes"),
    ("1", "Low", "42 samples, gives good results"),
    ("2", "Normal", "162 samples, smooth results, can be slow"),
    ("3", "High", "642 samples, very slow, very smooth results")
    ]
      
bpy.types.Scene.illuminato_quality = bpy.props.EnumProperty(items=numofrays,
                                                         default="1",
                                                         name="Quality",
                                                         description="The number of directions to search for light")

falloffs = [
    ("quad", "Quadratic", "Gives darker results"),
    ("lin", "Linear", "Default lighting gradient"),
    ("invq", "Inverse Quadratic", "Gives lighter results")
    ]
      
bpy.types.Scene.illuminato_falloff = bpy.props.EnumProperty(items=falloffs,
                                                            default="lin",
                                                            name="Falloff",
                                                            description="Falloff type for the shading")

class IlluminatoOperator(bpy.types.Operator):
    """Bake per vertex lighting"""
    bl_idname = "scene.illuminato"
    bl_label = "Illuminate"
    bl_options = {'REGISTER', 'UNDO'}
    
    @classmethod
    def poll(cls, context):
        return hasattr(context.active_object.data, 'polygons')

    def generateRays(self, n):
        t = (1.0 + 2.23606797749979) / 2.0
        verts = []
        tris = []
        
        # 12 vertices of the icosaherdon
        verts.append(Vector((-1, t, 0)))
        verts.append(Vector(( 1, t, 0)))
        verts.append(Vector((-1,-t, 0)))
        verts.append(Vector(( 1,-t, 0)))
        
        verts.append(Vector(( 0,-1, t)))
        verts.append(Vector(( 0, 1, t)))
        verts.append(Vector(( 0,-1,-t)))
        verts.append(Vector(( 0, 1,-t)))
        
        verts.append(Vector(( t, 0,-1)))
        verts.append(Vector(( t, 0, 1)))
        verts.append(Vector((-t, 0,-1)))
        verts.append(Vector((-t, 0, 1)))
        
        # 20 tris of the icosahedron
        tris.append((0, 11, 5))
        tris.append((0, 5, 1))
        tris.append((0, 1, 7))
        tris.append((0, 7, 10))
        tris.append((0, 10, 11))
        
        tris.append((1, 5, 9))
        tris.append((5, 11, 4))
        tris.append((11, 10, 2))
        tris.append((10, 7, 6))
        tris.append((7, 1, 8))
        
        tris.append((3, 9, 4))
        tris.append((3, 4, 2))
        tris.append((3, 2, 6))
        tris.append((3, 6, 8))
        tris.append((3, 8, 9))
        
        tris.append((4, 9, 5))
        tris.append((2, 4, 11))
        tris.append((6, 2, 10))
        tris.append((8, 6, 7))
        tris.append((9, 8, 1))
        
        # subdivide
        for i in range(n):
            new_tris = []
            
            for t in tris:
                a = (verts[t[0]] + verts[t[1]]).normalized()
                b = (verts[t[1]] + verts[t[2]]).normalized()
                c = (verts[t[2]] + verts[t[0]]).normalized()
                
                verts.append(a)
                verts.append(b)
                verts.append(c)
                
                new_tris.append((t[0], verts.index(a), verts.index(c)))
                new_tris.append((t[1], verts.index(b), verts.index(a)))
                new_tris.append((t[2], verts.index(c), verts.index(b)))
                new_tris.append((verts.index(a), verts.index(b), verts.index(c)))
            
            tris = new_tris
        self.rays = verts
    
    def getLocalAO(self, context, vertex, ob, distance):
        pos = vertex.co
        normal = vertex.normal
        rays = [r for r in self.rays if r.dot(normal) > -0.3 ]
        hits = len(rays)
        for ray in rays:
            ray_start_position = pos + (normal * .001)
            ray_end_position = ray_start_position + (ray * distance)
                        
            # Cast the ray
            hit_position, hit_normal, hit_poly = ob.ray_cast(ray_start_position, ray_end_position)
                        
            if hit_poly == -1:
                hits -= 1
            else:
                difference = ray_start_position - hit_position
                factor = difference.magnitude / distance
                hits -= factor**2
        
        return hits / len(rays)
    
    def getGlobalAO(self, context, vertex, ob, distance):
        pos = ob.matrix_world * vertex.co
        rot = ob.matrix_world.to_3x3()
        normal = rot * vertex.normal
        normal.normalize()
        rays = [r for r in self.rays if r.dot(normal) > -0.3 ]
        hits = len(rays)
        for ray in rays:
            ray_start_position = pos + (normal * .001)
            ray_end_position = ray_start_position + (ray * distance)
                        
            # Cast the ray
            hit, j, l, hit_position, hit_normal = context.scene.ray_cast(ray_start_position, ray_end_position)
                        
            if hit == False:
                hits -= 1
            else:
                difference = ray_start_position - hit_position
                factor = difference.magnitude / distance
                hits -= factor**2
        
        return hits / len(rays)
        
    def execute(self, context):
        vertex_color_layer = 'Illuminated'
        distance = context.scene.illuminato_distance
        samples = int(context.scene.illuminato_quality)
        local = context.scene.illuminato_localy

        horizon_color = bpy.data.worlds[0].horizon_color
        ambient_color = bpy.data.worlds[0].ambient_color
        
        self.generateRays(samples)
        
        if local == True: getAO = self.getLocalAO
        else: getAO = self.getGlobalAO
        
        for ob in context.selected_objects:
            if hasattr(ob.data, 'polygons'):
                mesh = ob.data
                polygons = mesh.polygons
                vertices = mesh.vertices
                
                if not vertex_color_layer in ob.data.vertex_colors: ob.data.vertex_colors.new(vertex_color_layer)        
                vertex_color = ob.data.vertex_colors[vertex_color_layer].data
                
                ambient_occlusion = []
                
                for vertex in vertices:
                    occlusion = 1-getAO(context, vertex, ob, distance)
                    if context.scene.illuminato_falloff == 'quad': occlusion = occlusion**2
                    elif context.scene.illuminato_falloff == 'invq': occlusion = occlusion**0.5
                    
                    ambient_occlusion.append(list(Color(ambient_color) + (Color(horizon_color) * occlusion)))
                        
                colors = ambient_occlusion
                
                vs = 0
                for i in range(len(polygons)):
                    polygon = polygons[i]
                    n = len(polygon.vertices)
                    
                    for vertex_index in range(n):
                        vertex_color[vs + vertex_index].color = colors[polygon.vertices[vertex_index]]
                    
                    vs += n
        return {'FINISHED'}
 
 
class IlluminatoPanel(bpy.types.Panel):
    """ Illuminato : Per vertex light baking """
    bl_label = "Illuminato"
    bl_idname = "scene.illuminato.panel"
    bl_space_type = 'PROPERTIES'
    bl_region_type = 'WINDOW'
    bl_context = "render"
 
    def draw(self, context):
        layout = self.layout
        scene = context.scene
        
        layout.operator('scene.illuminato', text='Illuminate', icon='SCENE')
        
        row = layout.row(align=True)
        row.prop(scene, "illuminato_quality")
        row.prop(scene, "illuminato_falloff")
        
        row = layout.row(align=True)
        row.prop(scene, "illuminato_distance")
        row.prop(scene, "illuminato_localy")

   
def register():
    bpy.utils.register_module(__name__)
    
def unregister():
    bpy.utils.unregister_module(__name__)
 
if __name__ == "__main__":
    register()