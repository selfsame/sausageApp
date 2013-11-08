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

# <pep8-80 compliant>

"""
This script exports Stanford PLY files from Blender. It supports normals,
colors, and texture coordinates per face or per vertex.
Only one mesh can be exported at a time.
"""

import bpy, bmesh
import os


















def get_scene_data():
    scene = bpy.context.scene
    gravity = scene.SAUSAGE_scene_gravity
    horizon = scene.world.horizon_color
    result_str = "gravity:{0:.2f},".format(*tuple( [gravity] )) 
    result_str += "background:[{0:.2f}, {1:.2f}, {2:.2f}, 1.0], ".format(*tuple( horizon )) 
    return result_str


def pack_dynamic_data(obj, global_matrix, global_scale):
    loc = obj.location*global_matrix
    result = '{type:"'+obj.SAUSAGE_physics_type+'",\n'
    result += 'name:"'+obj.name+'",\n'
    result += "position:[{0:.4f}, {1:.4f}, {2:.4f}], ".format(*tuple( loc ))
    result += "radius:"+str(obj.game.radius*global_scale)+", \n"
    result += "friction:"+str(obj.SAUSAGE_physics_friction)+", \n"
    result += "restitution:"+str(obj.SAUSAGE_physics_restitution)+", \n"
    result += "mass:"+str(obj.SAUSAGE_physics_mass)+", \n"
    result += "classes:["+pack_game_classes(obj)+"]}\n"
    return result

def pack_game_classes(obj):
    temp = []
    for sclass in obj.SAUSAGE_class:
        temp.append('"'+sclass.name+'"')
    return ", ".join(temp)


def get_ordered_vertex_loops(obj):
    ordered_loops = []
    bm = bmesh.new() 
    mesh = obj.data.copy()
    
    if not mesh:
        raise Exception("Error, could not get mesh data from active object")

    bm.from_mesh(mesh)

    def walk(index_list, vert):
        for edge in vert.link_edges:
            for v in edge.verts:
                if v.index not in index_list:
                    index_list.append(v.index)
                    return v
        return False

    for vv in bm.verts:
        found = False
        for loop in ordered_loops:
            if  vv.index in loop:
                found = True
        if not found:
            new_loop = []

            target = vv
            new_loop.append(target.index)
            while target != False:
                target = walk(new_loop, target)
            ordered_loops.append(new_loop)
    


    

    return ordered_loops






def save_selected(filepath,
              objects, box2d_objects, wireframe_objects, locii, sensors, dynamics,
              use_normals=True,
              use_uv_coords=True,
              use_colors=True,
              global_matrix=False,
              unscaled_matrix=False, global_scale=1.0):
    def rvec3d(v):
        return round(v[0], 6), round(v[1], 6), round(v[2], 6)

    def rvec2d(v):
        return round(v[0], 6), round(v[1], 6)

    file = open(filepath, "w", encoding="utf8", newline="\n")
    fw = file.write

    vertices_str = ""
    vertex_indices_str = ""
    vertex_objects = ""
    texture_objects = ""
    dynamic_objects = ""

    tex_vertices_str = ""
    tex_vertex_indices_str = ""
    tex_object_count = 0

    collide_groups_str = ""
    sensor_groups_str = ""
    wire_vertices_str = "[  "
    wire_indicies_str = "[  "
    camera_str = "camera:[0.0, 0.0, 2.0], "
    locii_str = ""
    object_count = 0
    total_wire_verts = 0

    temp = []
    for obj in dynamics:
        temp.append(pack_dynamic_data(obj, global_matrix, global_scale))
    dynamic_objects = ", ".join(temp)
    
    
    v_count = 0
    for obj in wireframe_objects:
        bm = bmesh.new() 
        mesh = obj.data.copy()
        
        if not mesh:
            raise Exception("Error, could not get mesh data from active object")
        mesh.transform(global_matrix * obj.matrix_world)
        bm.from_mesh(mesh)

        

        first = False
        color = bm.loops.layers.color[0]
        for edge in bm.edges:
            if edge.smooth == False:
                
                for vert in edge.verts:
                    darkest = 4
                    for ll in vert.link_loops:
                        col = ll[color]
                        t = col[0]+col[1]+col[2]
                        if t < darkest:
                            darkest = t
                            c = col
                    co = [vert.co[0], vert.co[1], vert.co[2]+.001]
                    wire_vertices_str += "{0:.4f}, {1:.4f}, {2:.4f}, ".format(*tuple(co)) 
                    if c:
                        wire_vertices_str += "{0:.2f}, {1:.2f}, {2:.2f}, 1.0, ".format(*tuple( c )) # col
                    else:
                        wire_vertices_str += "0.0, 0.0, 0.0, 1.0, "
                    wire_indicies_str += "{0}, ".format(v_count)
                    v_count += 1


                    


    if wire_vertices_str != "[":
        wire_vertices_str = wire_vertices_str[:-2]
    if wire_indicies_str != "[":
        wire_indicies_str = wire_indicies_str[:-2]
    

    for obj in box2d_objects:
        bm = bmesh.new() 
        mesh = obj.data.copy()
        mesh.transform(global_matrix * obj.matrix_world)
        bm.from_mesh(mesh)
        verts = bm.verts

        loops = get_ordered_vertex_loops(obj)
        for loop in loops:
            if len(loop) > 0:
                
                loopset = "{mask: "+str(obj.SAUSAGE_int)+",verts:["
                
                for i in loop:
                    loopset += "{0:.4f}, {1:.4f}, ".format(*tuple(verts[i].co[0:2]))
                if len(loopset) > 1:
                    loopset = loopset[:-2]
                collide_groups_str += loopset + "]}\n, "


    for obj in sensors:
        bm = bmesh.new() 
        mesh = obj.data.copy()
        
        if not mesh:
            raise Exception("Error, could not get mesh data from active object")
        mesh.transform(global_matrix * obj.matrix_world)
        bm.from_mesh(mesh)
        verts = bm.verts

        loc = obj.location*global_matrix

        loopset = ('{tag: "'+ obj.SAUSAGE_tag_name +'", usage: "'+ obj.SAUSAGE_sensor_usage +'", '+ ("loc:[{0:.4f}, {1:.4f}, {2:.4f}], ".format(*tuple( loc ))) + 
            "enter:"+str(obj.SAUSAGE_sensor_enter).lower()+", "+
            "exit:"+str(obj.SAUSAGE_sensor_exit).lower()+", "+
            "istring: \""+str(obj.SAUSAGE_string) +"\", "+
            "imod: "+str(obj.SAUSAGE_int) + ", fmod: "+str(obj.SAUSAGE_float) +', verts:[')
        

        for v in verts:
            loopset += "{0:.4f}, {1:.4f}, ".format(*tuple(v.co[0:2]))

        if len(loopset) > 1:
            loopset = loopset[:-2]
        sensor_groups_str += loopset + "]}\n, "

    for obj in locii:
        loc = obj.location*global_matrix
        locii_str += '{tag: "'+ obj.SAUSAGE_tag_name +'", usage: "'+ obj.SAUSAGE_locus_usage +'", fmod: '+ str(obj.SAUSAGE_float) +", "+ "pos:[{0:.4f}, {1:.4f}]".format(*tuple( loc )) + "}, " 
    if locii_str != "":
        locii_str = locii_str[:-2]


    for obj in objects:
        bpy.context.scene.objects.active = obj
        if obj.type == "CAMERA":
            loc = obj.location*global_matrix
            camera_str = "camera:[{0:.4f}, {1:.4f}, {2:.4f}], ".format(*tuple( loc ))
        elif not obj.data:
            print("no mesh on object ", obj.name)
        else: 
            object_count = 0
            mesh = obj.data.copy()

            vertices_str = ""
            vertex_indices_str = ""
            uses_alpha = False
            use_texture = False
            use_colors = True

            object_loc = global_matrix * obj.location
            object_loc_str = "position:[{0:.4f}, {1:.4f}, {2:.4f}], ".format(*tuple( object_loc ))

            from mathutils import Matrix

            obj_mat4 = global_matrix * obj.matrix_world #* Matrix.Scale(1/global_scale, 4)    
            obj_mat4_str = ("\nlocal_mat4:["+
                            "{0:.3f}, {1:.3f}, {2:.3f}, {3:.3f},".format(*tuple( obj_mat4[0] )) +
                            "{0:.3f}, {1:.3f}, {2:.3f}, {3:.3f},".format(*tuple( obj_mat4[1] )) +
                            "{0:.3f}, {1:.3f}, {2:.3f}, {3:.3f},".format(*tuple( obj_mat4[2] )) +
                            "{0:.3f}, {1:.3f}, {2:.3f}, {3:.3f} ".format(*tuple( obj_mat4[3] )) +
                            "]\n, ")
            decom = obj_mat4.decompose()
            oloc = decom[0]
            oquat = (global_matrix * obj.matrix_world).decompose()[1]
            oscale = (unscaled_matrix * obj.matrix_world).decompose()[2]

            decomp_str = "position:[{0:.4f}, {1:.4f}, {2:.4f}], ".format(*tuple( oloc ))
            decomp_str += "scale:[{0:.4f}, {1:.4f}, {2:.4f}], ".format(*tuple( oscale ))
            decomp_str += "quaternion:[{0:.4f}, {1:.4f}, {2:.4f}, {3:.4f}], ".format(*tuple( oquat ))

            if len(obj.data.uv_layers) > 0:
                use_texture = True
                has_uv = True
                use_uv_coords = True
            else:
                use_uv_coords = False


            if not mesh:
                raise Exception("Error, could not get mesh data from active object")
            mesh.transform(Matrix.Scale(global_scale, 4)  ) #* obj.matrix_world)

            # Be sure tessface & co are available!
            mesh.calc_tessface()

            has_uv = bool(mesh.tessface_uv_textures)
            has_vcol = bool(mesh.tessface_vertex_colors)
            print(obj.name, has_vcol)
            if not has_uv:
                #print ("FAIL bool(mesh.tessface_uv_textures)")
                use_uv_coords = False
            if not has_vcol:
                use_colors = False

            if not use_uv_coords:
                has_uv = False
            if not use_colors:
                has_vcol = False

            if has_uv:
                active_uv_layer = mesh.tessface_uv_textures.active
                if not active_uv_layer:
                    #print ("FAIL mesh.tessface_uv_textures.active")
                    use_uv_coords = False
                    has_uv = False
                else:
                    active_uv_layer = active_uv_layer.data

            alpha_vertex_color = False
            if has_vcol:
                active_col_layer = mesh.tessface_vertex_colors[0]
                if 'alpha' in mesh.tessface_vertex_colors:
                    print("HAVE ALPHA:", obj.name)
                    has_uv
                    uses_alpha = True
                    alpha_vertex_color = mesh.tessface_vertex_colors['alpha'].data
                if not active_col_layer:
                    print("FAIL : active_col_layer", obj.name)
                    use_colors = False
                    has_vcol = False
                else:
                    active_col_layer = active_col_layer.data

            # in case
            color = uvcoord = uvcoord_key = normal = normal_key = None

            mesh_verts = mesh.vertices  # save a lookup
            ply_verts = []  # list of dictionaries
            # vdict = {} # (index, normal, uv) -> new index
            vdict = [{} for i in range(len(mesh_verts))]
            ply_faces = [[] for f in range(len(mesh.tessfaces))]
            vert_count = 0
            for i, f in enumerate(mesh.tessfaces):

                smooth = not use_normals or f.use_smooth
                if not smooth:
                    normal = f.normal[:]
                    normal_key = rvec3d(normal)

                if has_uv:
                    uv = active_uv_layer[i]
                    uv = uv.uv1, uv.uv2, uv.uv3, uv.uv4
                if has_vcol:
                    col = active_col_layer[i]
                    if bool(alpha_vertex_color):

                        cola = alpha_vertex_color[i]
                        #cola = col.color1[0], col.color2[0], col.color3[0], col.color4[0]
                        col = col.color1[:] + (cola.color1[0],0), col.color2[:] + (cola.color2[0],0), col.color3[:] + (cola.color3[0],0), col.color4[:] + (cola.color4[0],0)
                    else:
                        col = col.color1[:] + (1,0), col.color2[:] + (1,0), col.color3[:] + (1,0), col.color4[:] + (1,0)

                f_verts = f.vertices

                pf = ply_faces[i]
                for j, vidx in enumerate(f_verts):
                    v = mesh_verts[vidx]

                    if smooth:
                        normal = v.normal[:]
                        normal_key = rvec3d(normal)

                    if has_uv:
                        uvcoord = uv[j][0], uv[j][1]
                        uvcoord_key = rvec2d(uvcoord)

                    if has_vcol:
                        color = col[j]
                        color = (color[0], color[1], color[2], color[3])
                    key = normal_key, uvcoord_key, color

                    vdict_local = vdict[vidx]
                    pf_vidx = vdict_local.get(key)  # Will be None initially

                    if pf_vidx is None:  # same as vdict_local.has_key(key)
                        pf_vidx = vdict_local[key] = vert_count
                        ply_verts.append((vidx, normal, uvcoord, color))
                        vert_count += 1

                    pf.append(pf_vidx)

            

            for i, v in enumerate(ply_verts):
                vertices_str += "{0:.4f}, {1:.4f}, {2:.4f}, ".format(*tuple(mesh_verts[v[0]].co[:]))  # co
                if use_uv_coords:
                    vertices_str += "{0:.2f}, {1:.2f}, ".format(*tuple( [v[2][0], 1.0-v[2][1]] ))
                if use_colors:
                    vertices_str += "{0:.2f}, {1:.2f}, {2:.2f}, {3:.2f}, ".format(*tuple( v[3] )) # col
            
            highest = 0
                
            for pf in ply_faces:
                lb = 9999999
                ub = -9999999
                for k in pf:
                    if k < lb: lb = k
                    if k > ub: ub = k
                dif = ub - lb
                if dif > highest:
                    highest = dif
                if len(pf) < 3:
                    print("WARNING: FACE WITH < 3 vertices:", pf)
                if len(pf) == 3:
                    vertex_indices_str += "{0}, {1}, {2}, ".format(*tuple([pf[0]+object_count,pf[1]+object_count,pf[2]+object_count]))
                elif len(pf) == 4:
                    vertex_indices_str += "{0}, {1}, {2}, {0}, {2}, {3}, ".format(*tuple([pf[0]+object_count,pf[1]+object_count,pf[2]+object_count,pf[3]+object_count]))
                else:
                    print("WARNING: FACE WITH > 4 vertices:", pf)
            object_count += len(ply_verts)
            print("INDEX RANGE: ", highest)
            if use_uv_coords:
                if obj.SAUSAGE_alpha_texture:
                    uses_alpha = True
                texture_objects += '\n\n{name:"'+obj.name+'",\n '+ "classes:["+pack_game_classes(obj)+"],\n " + "texture:true,\n "+decomp_str + obj_mat4_str +" static_vertices:["+vertices_str[0:-2]+"],\n"+"static_indicies:["+vertex_indices_str[0:-2]+"],\n alpha:"+str(uses_alpha).lower()+"}, "
            else:
                vertex_objects += '\n\n{name:"'+obj.name+'",\n '+ "classes:["+pack_game_classes(obj)+"],\n " + "texture:false,\n "+decomp_str + obj_mat4_str + " static_vertices:["+vertices_str[0:-2]+"],\n"+"static_indicies:["+vertex_indices_str[0:-2]+"],\n alpha:"+str(uses_alpha).lower()+"}, "













    


    fw("{"+get_scene_data()+
        "vertex_objects:["+vertex_objects[0:-2]+"],\n\n"+
        "texture_objects:["+texture_objects[0:-2]+"],\n\n"+

        "wire_vertices:"+wire_vertices_str+"],\n"+
        "wire_indicies:"+wire_indicies_str+"],\n"+
        "collide_groups:["+collide_groups_str[0:-2]+"],\n"+
        "dynamic_objects:["+dynamic_objects+"],\n"+
        "sensor_groups:["+sensor_groups_str[0:-2]+"],\n"+
        "locii:["+locii_str +"]}")
    file.close()
    print("writing %r done" % filepath)

    return {'FINISHED'}













def save(operator,
         context,
         filepath="",
         use_mesh_modifiers=True,
         use_normals=True,
         use_uv_coords=True,
         use_colors=True,
         global_matrix=None,
         unscaled_matrix=None,
         global_scale = 1.0
         ):

    scene = context.scene
    obj = context.selected_objects

    objects = []
    tex_objects = []
    wireframe_objects = []
    box2d_objects = []
    locii = []
    sensors = []
    dynamics = []

    for oo in context.selected_objects:
        if oo.type != 'LAMP':
            if oo.type == 'MESH' and oo.SAUSAGE_visible_object:
                #if oo.type != 'CAMERA' and len(oo.data.uv_layers) > 0:
                #    tex_objects.append(oo)
                #else:
                objects.append(oo)
            if oo.SAUSAGE_wireframe_object:
                wireframe_objects.append(oo)
            
            if oo.SAUSAGE_locus:
                locii.append(oo)
            if oo.SAUSAGE_sensor_area:
                sensors.append(oo)
            if oo.SAUSAGE_box2d or oo.SAUSAGE_physics_edges:
                if oo.SAUSAGE_physics_dynamic == False:
                    box2d_objects.append(oo)
                else:
                    dynamics.append(oo)

    if global_matrix is None:
        from mathutils import Matrix
        global_matrix = Matrix()
    if unscaled_matrix is None:
        from mathutils import Matrix
        unscaled_matrix = Matrix()

    if bpy.ops.object.mode_set.poll():
        bpy.ops.object.mode_set(mode='OBJECT')





    ret = save_selected(filepath, objects, box2d_objects, wireframe_objects, locii, sensors, dynamics,
                    use_normals=use_normals,
                    use_uv_coords=use_uv_coords,
                    use_colors=use_colors,
                    global_matrix=global_matrix,
                    unscaled_matrix = unscaled_matrix, global_scale = global_scale

                    )



    return ret
