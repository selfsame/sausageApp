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

# <pep8 compliant>

bl_info = {
    "name": "Export Materials to GLSL",
    "author": "Vitor Balbio",
    "version": (1, 0),
    "blender": (2, 6, 0),
    "api": 40838,
    "location": "Material Properties",
    "description": "Export one or more materials to GLSL Code",    
    "warning": "",
    "wiki_url": "http://wiki.blender.org/index.php/Extensions:2.5/Py/Scripts/Game_Engine/Export_GLSL",
    "tracker_url": "http://projects.blender.org/tracker/index.php?func=detail&aid=28839&group_id=153&atid=467",
    "category": "Game Engine"}
    
import bpy
import gpu
import os

class ExportGLSL(bpy.types.Operator):
    """Export all materials of this scene to GLSL (.frag and .vert files) """
    bl_idname = "export.glslmat"
    bl_label = "Export Material To GLSL"

    filepath = bpy.props.StringProperty(subtype="FILE_PATH")

    #@classmethod
    #def poll(cls, context):
        #return context.object is not None

    def execute(self, context):
        
        if bpy.context.scene.exportGlslOptions == "0":
            Scene = bpy.context.scene
            Materials = bpy.data.materials
            
            #self.filepath = bpy.path.relpath(self.filepath)
            self.filepath = os.path.dirname(self.filepath)
            
            for mat in Materials:
    
                Shader = gpu.export_shader(Scene,mat)
                frag = open(self.filepath + "\mat_" + mat.name + ".frag","w")
                vertex = open(self.filepath + "\mat_" + mat.name + ".vert" ,"w")
                
                frag.write(Shader["fragment"])
                vertex.write(Shader["vertex"])
                
        elif bpy.context.scene.exportGlslOptions == "1":
            Scene = bpy.context.scene
            
            ObjList = bpy.context.selected_objects
            for obj in ObjList:
                print()
                print(obj)
                for matSL in obj.material_slots:
                    
                    mat = matSL.material
                    #print(mat)
            
                    #self.filepath = bpy.path.relpath(self.filepath)
                    path = os.path.dirname(self.filepath)
                    print(path)
            
                    Shader = gpu.export_shader(Scene,mat)
                    
                    frag = open(path + "\mat_" + mat.name + ".frag","w")
                    vertex = open(path + "\mat_" + mat.name + ".vert" ,"w")
                    
                    frag.write(Shader["fragment"])
                    vertex.write(Shader["vertex"])
                
        elif bpy.context.scene.exportGlslOptions == "2":
            Scene = bpy.context.scene
            mat = bpy.context.material
            
            #self.filepath = bpy.path.relpath(self.filepath)
            self.filepath = os.path.dirname(self.filepath)
    
            Shader = gpu.export_shader(Scene,mat)
            frag = open(self.filepath + "\mat_" + mat.name + ".frag","w")
            vertex = open(self.filepath + "\mat_" + mat.name + ".vert" ,"w")
            
            frag.write(Shader["fragment"])
            vertex.write(Shader["vertex"])
        
                
        return {'FINISHED'}

    def invoke(self, context, event):
        context.window_manager.fileselect_add(self)
        return {'RUNNING_MODAL'}


class GlslExportPanel(bpy.types.Panel):
    bl_idname = "Export_Material2GLSL"
    bl_label = "Export Material To GLSL"
    bl_space_type = 'PROPERTIES'
    bl_region_type = 'WINDOW'
    bl_context = "material"
    
    #bpy.types.Material.exportallmat = bpy.props.BoolProperty(name="All Materials of This Object")
    bpy.types.Scene.exportallmat = bpy.props.BoolProperty(name = "All Materials")
    bpy.types.Scene.exportselectmat = bpy.props.BoolProperty(name = "Active Material")
    
    bpy.types.Scene.exportGlslOptions = bpy.props.EnumProperty(name="Export",
            description="",
            items=(
                   ("0", "All Materials", "Export all materials from all scenes"),
                   ("1", "Selected Object Materials ", "Export all materials from the selected objects"),
                   ("2", "Active Material", "Export just this active material")
                   ),
            default = "0")
    
    def draw(self, context):
        #self.layout.label(text="Export Material To GLSL")
        mat = context.material
        #self.layout.prop(bpy.context.scene, "exportallmat")
        self.layout.prop(context.scene, "exportGlslOptions")
        self.layout.operator("export.glslmat")

# Register and add to the file selector
def register():
    bpy.utils.register_class(ExportGLSL)
    bpy.utils.register_class(GlslExportPanel)
    #bpy.utils.register_class(Scatter)
    #bpy.types.INFO_MT_mesh_add.append(menu_func)


def unregister():
    bpy.utils.unregister_class(ExportGLSL)
    bpy.utils.unregister_class(GlslExportPanel)
   # bpy.types.INFO_MT_mesh_add.remove(menu_func)
