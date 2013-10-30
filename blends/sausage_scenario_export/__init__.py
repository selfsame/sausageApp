

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

bpy.types.Scene.SAUSAGE_scene_gravity = bpy.props.FloatProperty(default = 20.0)

#bpy.types.MeshEdge.SAUSAGE_STATIC = bpy.props.BoolProperty()
class SCENE_PT_hello( bpy.types.Panel ):

    bl_label = "Sausage Scenario Settings"
    bl_space_type = "PROPERTIES"
    bl_region_type = "WINDOW"
    bl_context = "scene"

    def draw(self, context):
        layout = self.layout
        scene = context.scene

        row = layout.row()
        row.label(text="Sausage Scene")
        row = layout.row()
        row.prop( scene, "SAUSAGE_scene_gravity", text="game gravity" )

bpy.types.Object.SAUSAGE_box2d = bpy.props.BoolProperty()
bpy.types.Object.SAUSAGE_physics_edges = bpy.props.BoolProperty()
bpy.types.Object.SAUSAGE_physics_dynamic = bpy.props.BoolProperty()
bpy.types.Object.SAUSAGE_collision_mask = bpy.props.IntProperty(default = 0)
bpy.types.Object.SAUSAGE_physics_friction = bpy.props.FloatProperty(default = .2)
bpy.types.Object.SAUSAGE_physics_restitution = bpy.props.FloatProperty(default = .2)
bpy.types.Object.SAUSAGE_physics_mass = bpy.props.FloatProperty(default = 10.0)
bpy.types.Object.SAUSAGE_physics_type = EnumProperty(name="Physics Type",
            items=(('circle', "circle", ""),
                   ('rect', "rect", ""),
                   ('polygon', "polygon", "")
                   ),
            default='polygon'
            )


bpy.types.Object.SAUSAGE_visible_object = bpy.props.BoolProperty(default = True)
bpy.types.Object.SAUSAGE_wireframe_object = bpy.props.BoolProperty()
bpy.types.Object.SAUSAGE_alpha_texture = bpy.props.BoolProperty()
bpy.types.Object.SAUSAGE_sensor_area = bpy.props.BoolProperty()
bpy.types.Object.SAUSAGE_tag_name = bpy.props.StringProperty()
bpy.types.Object.SAUSAGE_float = bpy.props.FloatProperty(default = 0.0)
bpy.types.Object.SAUSAGE_int = bpy.props.IntProperty(default = 1)
bpy.types.Object.SAUSAGE_string = bpy.props.StringProperty()
bpy.types.Object.SAUSAGE_sensor_enter = bpy.props.BoolProperty(default = True)
bpy.types.Object.SAUSAGE_sensor_exit = bpy.props.BoolProperty()
bpy.types.Object.SAUSAGE_sensor_usage = EnumProperty(name="Sensor Usage",
            items=(('GENERIC', "generic", ""),
                   ('PORTAL', "portal", ""),
                   ('DEATH', "death", ""),
                   ('MASK', "change mask", ""),
                   ('CHANGEZ', "change player Z", ""),
                   ('GRAVITY', "change gravity", ""),
                   ('ZOOM', "camera zoom", ""),
                   ('MESSAGE', "show message", ""),
                   ('GAME', "game", "")
                   ),
            default='GENERIC'
            )

bpy.types.Object.SAUSAGE_locus = bpy.props.BoolProperty()
bpy.types.Object.SAUSAGE_locus_usage = EnumProperty(name="Locus Usage",
            items=(('PLAYER_SPAWN', "player spawn", ""),
                   ('PICKUP_SPAWN', "pickup spawn", ""),
                   ('GENERIC', "generic", "")
                   ),
            default='GENERIC'
            )



class SausageClass(bpy.types.PropertyGroup):
    name = bpy.props.StringProperty(name="Test Prop", default="Unknown")
 
bpy.utils.register_class(SausageClass)
bpy.types.Object.SAUSAGE_class = bpy.props.CollectionProperty(type=SausageClass)
bpy.types.Object.SAUSAGE_class_index = bpy.props.IntProperty(default=-1)
 

class SAUSAGE_UL_class(bpy.types.UIList):

    def draw_item(self, context, layout, data, item, icon, active_data, active_propname, index):
        ob = data
        sclass = item
        if self.layout_type in {'DEFAULT', 'COMPACT'}:
            layout.label(text=sclass.name if sclass.name else "", translate=False, icon_value=icon)

        # 'GRID' layout type should be as compact as possible (typically a single icon!).
        elif self.layout_type in {'GRID'}:
            layout.alignment = 'CENTER'
            layout.label(text="", icon_value=icon)

bpy.utils.register_class(SAUSAGE_UL_class)

class MY_LIST_OT_add(bpy.types.Operator):
        bl_idname      = 'sausage_class.add'
        bl_label       = "Add list item"
        bl_description = "Add list item"
 
        def invoke(self, context, event):
                obj = context.active_object
                obj.SAUSAGE_class.add()
                return{'FINISHED'}
class MY_LIST_OT_remove(bpy.types.Operator):
        bl_idname      = 'sausage_class.remove'
        bl_label       = "Add list item"
        bl_description = "Add list item"
 
        def invoke(self, context, event):
                obj = context.active_object
                obj.SAUSAGE_class.remove(obj.SAUSAGE_class_index)
                return{'FINISHED'}





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

        if obj.type == "EMPTY":
            column = layout.column()
            column.prop( obj, "SAUSAGE_locus", text="Locus")

            if obj.SAUSAGE_locus == True:
                box = layout.box()
                box.prop( obj, "SAUSAGE_tag_name", text="Tag" )
                box.prop( obj, "SAUSAGE_locus_usage", text="Usage" )
                box.prop( obj, "SAUSAGE_int", text="int" )
                box.prop( obj, "SAUSAGE_float", text="float" )
                box.prop( obj, "SAUSAGE_string", text="string" )
        elif obj.type == "MESH":

            row = layout.row()
            row.prop( obj, "SAUSAGE_visible_object", text="Visible" )

            
            
            row = layout.row()
            row.prop( obj, "SAUSAGE_box2d", text="Box2D" )
            if obj.SAUSAGE_box2d == True:
                row.prop( obj, "SAUSAGE_physics_type", text="" )
                box = layout.box()
                row = box.row()
                
                col = row.column()
                col.prop( obj, "SAUSAGE_int", text="mask" )
                col.prop( obj, "SAUSAGE_physics_friction", text="friction" )
                col.prop( obj, "SAUSAGE_physics_restitution", text="restitution" )
                col.prop( obj, "SAUSAGE_physics_mass", text="mass" )
                col = row.column()
                col.prop( obj, "SAUSAGE_physics_dynamic", text="Box2D dynamic" )
                if obj.SAUSAGE_physics_type == 'circle':
                    col.prop( obj.game, "radius", text="radius" )

            

            

                

            row = layout.row()
            row.prop( obj, "SAUSAGE_wireframe_object", text="Render as wireframe" )

            row = layout.row()
            row.prop( obj, "SAUSAGE_alpha_texture", text="Alpha texture" )

            column = layout.column()
            column.prop( obj, "SAUSAGE_sensor_area", text="Sensor Area")



            if obj.SAUSAGE_sensor_area == True:
                box = layout.box()
                
                row = box.row()
                row.label(text="trigger on")
                row.prop( obj, "SAUSAGE_sensor_enter", text="enter" )
                row.prop( obj, "SAUSAGE_sensor_exit", text="exit" )

                box.prop( obj, "SAUSAGE_tag_name", text="Tag" )
                box.prop( obj, "SAUSAGE_sensor_usage", text="Usage" )
                box.prop( obj, "SAUSAGE_int", text="Int" )
                box.prop( obj, "SAUSAGE_float", text="Float" )
                box.prop( obj, "SAUSAGE_string", text="string" )

        row = layout.row()
        row.separator()
        row = layout.row()
        row.label(text="game classes", icon="SOLID" )
        row = layout.row()
        side = row.split(.9)
        left = side.column()
        left.template_list("SAUSAGE_UL_class", "SAUSAGE_class_UI_LIST", obj, 'SAUSAGE_class', obj, 'SAUSAGE_class_index', rows=2)

        right = side.column()
        right.operator('sausage_class.add', text="", icon="ZOOMIN")
        
        if obj.SAUSAGE_class_index >= 0:
            sclass = obj.SAUSAGE_class[obj.SAUSAGE_class_index]
            right.operator('sausage_class.remove', text="", icon="ZOOMOUT")
            
            left.prop( sclass, "name", text="name" )
        #col.operator('my_list.add', text="", icon="ZOOMIN")




 





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
            default='-Z',
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
                                            "check_existing",
                                            "filter_glob",
                                            ))
        global_matrix = axis_conversion(to_forward=self.axis_forward,
                                        to_up=self.axis_up,
                                        ).to_4x4() * Matrix.Scale(self.global_scale, 4)
        unscaled_matrix = axis_conversion(to_forward=self.axis_forward,
                                        to_up=self.axis_up,
                                        ).to_4x4() 
        keywords["global_matrix"] = global_matrix
        keywords["unscaled_matrix"] = unscaled_matrix

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
