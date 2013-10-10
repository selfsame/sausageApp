

bl_info = {
    "name": "Sausage Scenario Export",
    "author": "jplur",
    "blender": (2, 57, 0),
    "location": "File > Import-Export",
    "description": "Export a sausage scenario",
    "warning": "",
    "wiki_url": "",
    "tracker_url": "",
    "category": "Import-Export"}


if "bpy" in locals():
    import imp
    if "export_sausage" in locals():
        imp.reload(export_sausage)



import os
import bpy
from bpy.props import (CollectionProperty,
                       StringProperty,
                       BoolProperty,
                       EnumProperty,
                       FloatProperty,
                       )
from bpy_extras.io_utils import (ImportHelper,
                                 ExportHelper,
                                 axis_conversion,
                                 )

bpy.types.Object.SAUSAGE_physics_edges = bpy.props.BoolProperty()
bpy.types.Object.SAUSAGE_visible_object = bpy.props.BoolProperty(default = True)
bpy.types.Object.SAUSAGE_wireframe_object = bpy.props.BoolProperty()

#bpy.types.MeshEdge.SAUSAGE_STATIC = bpy.props.BoolProperty()

class OBJECT_PT_hello( bpy.types.Panel ):

    bl_label = "Sausage Scenario"
    bl_space_type = "PROPERTIES"
    bl_region_type = "WINDOW"
    bl_context = "object"

    def draw(self, context):
        layout = self.layout
        obj = context.object

        row = layout.row()
        row.label(text="Selected object: " + obj.name )

        row = layout.row()
        row.prop( obj, "SAUSAGE_visible_object", text="Visible" )

        row = layout.row()
        row.prop( obj, "SAUSAGE_physics_edges", text="Box2D EdgeShape" )

        row = layout.row()
        row.prop( obj, "SAUSAGE_wireframe_object", text="render as wireframe" )

 





class ExportSausageScenario(bpy.types.Operator, ExportHelper):
    """Export a single object as a Stanford PLY with normals, """ \
    """colors and texture coordinates"""
    bl_idname = "export_mesh.json"
    bl_label = "Export Sausage Scenario"

    filename_ext = ".json"
    filter_glob = StringProperty(default="*.json", options={'HIDDEN'})

    use_mesh_modifiers = BoolProperty(
            name="Apply Modifiers",
            description="Apply Modifiers to the exported mesh",
            default=True,
            )
    use_normals = BoolProperty(
            name="Normals",
            description="Export Normals for smooth and "
                        "hard shaded faces "
                        "(hard shaded faces will be exported "
                        "as individual faces)",
            default=False,
            )
    use_uv_coords = BoolProperty(
            name="UVs",
            description="Export the active UV layer",
            default=False,
            )
    use_colors = BoolProperty(
            name="Vertex Colors",
            description="Export the active vertex color layer",
            default=True,
            )



    axis_forward = EnumProperty(
            name="Forward",
            items=(('X', "X Forward", ""),
                   ('Y', "Y Forward", ""),
                   ('Z', "Z Forward", ""),
                   ('-X', "-X Forward", ""),
                   ('-Y', "-Y Forward", ""),
                   ('-Z', "-Z Forward", ""),
                   ),
            default='Z',
            )
    axis_up = EnumProperty(
            name="Up",
            items=(('X', "X Up", ""),
                   ('Y', "Y Up", ""),
                   ('Z', "Z Up", ""),
                   ('-X', "-X Up", ""),
                   ('-Y', "-Y Up", ""),
                   ('-Z', "-Z Up", ""),
                   ),
            default='Y',
            )
    global_scale = FloatProperty(
            name="Scale",
            min=0.0001, max=1000.0,
            default=.03,
            )

    @classmethod
    def poll(cls, context):
        return context.active_object != None

    def execute(self, context):
        from . import export_sausage

        from mathutils import Matrix

        keywords = self.as_keywords(ignore=("axis_forward",
                                            "axis_up",
                                            "global_scale",
                                            "check_existing",
                                            "filter_glob",
                                            ))
        global_matrix = axis_conversion(to_forward=self.axis_forward,
                                        to_up=self.axis_up,
                                        ).to_4x4() * Matrix.Scale(self.global_scale, 4)
        keywords["global_matrix"] = global_matrix

        filepath = self.filepath
        filepath = bpy.path.ensure_ext(filepath, self.filename_ext)

        return export_sausage.save(self, context, **keywords)

    def draw(self, context):
        layout = self.layout

        row = layout.row()
        row.prop(self, "use_mesh_modifiers")
        row.prop(self, "use_normals")
        row = layout.row()
        row.prop(self, "use_uv_coords")
        row.prop(self, "use_colors")

        layout.prop(self, "axis_forward")
        layout.prop(self, "axis_up")
        layout.prop(self, "global_scale")



def menu_func_export(self, context):
    self.layout.operator(ExportSausageScenario.bl_idname, text="Sausage Scenario (.json)")


def register():
    bpy.utils.register_module(__name__)

    bpy.types.INFO_MT_file_export.append(menu_func_export)


def unregister():
    bpy.utils.unregister_module(__name__)

    bpy.types.INFO_MT_file_export.remove(menu_func_export)

if __name__ == "__main__":
    register()
