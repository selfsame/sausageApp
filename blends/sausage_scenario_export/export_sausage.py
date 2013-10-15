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


def save_mesh(filepath,
              mesh,
              use_normals=True,
              use_uv_coords=True,
              use_colors=True,
              global_matrix=False
              ):

    def rvec3d(v):
        return round(v[0], 6), round(v[1], 6), round(v[2], 6)

    def rvec2d(v):
        return round(v[0], 6), round(v[1], 6)

    file = open(filepath, "w", encoding="utf8", newline="\n")
    fw = file.write

    # Be sure tessface & co are available!
    if not mesh.tessfaces and mesh.polygons:
        mesh.calc_tessface()

    has_uv = bool(mesh.tessface_uv_textures)
    has_vcol = bool(mesh.tessface_vertex_colors)

    if not has_uv:
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
            use_uv_coords = False
            has_uv = False
        else:
            active_uv_layer = active_uv_layer.data

    if has_vcol:
        active_col_layer = mesh.tessface_vertex_colors.active
        if not active_col_layer:
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
            col = col.color1[:], col.color2[:], col.color3[:], col.color4[:]

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
                color = (int(color[0] * 255.0),
                         int(color[1] * 255.0),
                         int(color[2] * 255.0),
                         )
            key = normal_key, uvcoord_key, color

            vdict_local = vdict[vidx]
            pf_vidx = vdict_local.get(key)  # Will be None initially

            if pf_vidx is None:  # same as vdict_local.has_key(key)
                pf_vidx = vdict_local[key] = vert_count
                ply_verts.append((vidx, normal, uvcoord, color))
                vert_count += 1

            pf.append(pf_vidx)

    fw("ply\n")
    fw("format ascii 1.0\n")
    fw("comment Created by Blender %s - "
       "www.blender.org, source file: %r\n" %
       (bpy.app.version_string, os.path.basename(bpy.data.filepath)))

    fw("element vertex %d\n" % len(ply_verts))

    fw("property float x\n"
       "property float y\n"
       "property float z\n")

    if use_normals:
        fw("property float nx\n"
           "property float ny\n"
           "property float nz\n")
    if use_uv_coords:
        fw("property float s\n"
           "property float t\n")
    if use_colors:
        fw("property uchar red\n"
           "property uchar green\n"
           "property uchar blue\n")

    fw("element face %d\n" % len(mesh.tessfaces))
    fw("property list uchar uint vertex_indices\n")
    fw("end_header\n")

    for i, v in enumerate(ply_verts):
        fw("%.6f %.6f %.6f" % mesh_verts[v[0]].co[:])  # co
        if use_normals:
            fw(" %.6f %.6f %.6f" % v[1])  # no
        if use_uv_coords:
            fw(" %.6f %.6f" % v[2])  # uv
        if use_colors:
            fw(" %u %u %u" % v[3])  # col
        fw("\n")

    for pf in ply_faces:
        if len(pf) == 3:
            fw("3 %d %d %d\n" % tuple(pf))
        else:
            fw("4 %d %d %d %d\n" % tuple(pf))

    file.close()
    print("writing %r done" % filepath)

    return {'FINISHED'}





















def save_selected(filepath,
              objects, box2d_objects, wireframe_objects, spawn_points,
              use_normals=True,
              use_uv_coords=True,
              use_colors=True,
              global_matrix=False):
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

    tex_vertices_str = ""
    tex_vertex_indices_str = ""
    tex_object_count = 0

    collide_groups_str = ""
    wire_vertices_str = "["
    wire_indicies_str = "["
    camera_str = "camera:[0.0, 0.0, 2.0], "
    spawn_points_str = ""
    object_count = 0
    total_wire_verts = 0
    for obj in wireframe_objects:
        bm = bmesh.new() 
        mesh = obj.data.copy()
        
        if not mesh:
            raise Exception("Error, could not get mesh data from active object")
        mesh.transform(global_matrix * obj.matrix_world)
        bm.from_mesh(mesh)

        first = False
        color = bm.loops.layers.color[0]
        for face in bm.faces:
            
            valid = False
            starting_count = total_wire_verts
            make_line = False
            for iter in range(0,len(face.loops)-1):
                loop = face.loops[iter]
                ed = loop.edge 
                if ed.smooth == False or make_line == True:

                    wire_vertices_str += "{0:.4f}, {1:.4f}, {2:.4f}, ".format(*tuple(loop.vert.co[:])) 
                    c = loop[color]
                    if c:
                        wire_vertices_str += "{0:.2f}, {1:.2f}, {2:.2f}, 1.0, ".format(*tuple( c )) # col
                    else:
                        wire_vertices_str += "0.0, 0.0, 0.0, 1.0, "
                    
                    
                    if make_line:
                        wire_indicies_str += "{0}, ".format(total_wire_verts-1)
                        wire_indicies_str += "{0}, ".format(total_wire_verts)
                    total_wire_verts += 1
                if ed.smooth == False:
                    make_line = True
                    if iter == len(face.loops)-1:
                        
                        wire_indicies_str += "{0}, ".format(starting_count) 
                        wire_indicies_str += "{0}, ".format(total_wire_verts-1)
                else:
                    make_line = False

                    


    if wire_vertices_str != "[":
        wire_vertices_str = wire_vertices_str[:-2]+"]"
    if wire_indicies_str != "[":
        wire_indicies_str = wire_indicies_str[:-2]+"]"
    

    for obj in box2d_objects:
        bm = bmesh.new() 
        mesh = obj.data.copy()
        
        if not mesh:
            raise Exception("Error, could not get mesh data from active object")
        mesh.transform(global_matrix * obj.matrix_world)
        bm.from_mesh(mesh)
        verts = bm.verts



        loopset = "{verts:["
        for v in verts:
            loopset += "{0:.4f}, {1:.4f}, ".format(*tuple(v.co[0:2]))

        if len(loopset) > 1:
            loopset = loopset[:-2]
        collide_groups_str += loopset + "]}\n, "

    for obj in spawn_points:
        loc = obj.location*global_matrix
        spawn_points_str += "{"+ "pos:[{0:.4f}, {1:.4f}]".format(*tuple( loc )) + "}, " 
    if spawn_points_str != "":
        spawn_points_str = spawn_points_str[:-2]


    for obj in objects:
        bpy.context.scene.objects.active = obj
        if obj.type == "CAMERA":
            loc = obj.location*global_matrix
            camera_str = "camera:[{0:.4f}, {1:.4f}, {2:.4f}], ".format(*tuple( loc ))
        else: 
            v
            object_count = 0
            mesh = obj.data.copy()

            vertices_str = ""
            vertex_indices_str = ""
            uses_alpha = False
            use_texture = False

            

            if len(obj.data.uv_layers) > 0:
                use_texture = True
                has_uv = True
                use_uv_coords = True
            else:
                use_uv_coords = False


            if not mesh:
                raise Exception("Error, could not get mesh data from active object")
            mesh.transform(global_matrix * obj.matrix_world)

            # Be sure tessface & co are available!
            mesh.calc_tessface()

            has_uv = bool(mesh.tessface_uv_textures)
            has_vcol = bool(mesh.tessface_vertex_colors)

            if not has_uv:
                print ("FAIL bool(mesh.tessface_uv_textures)")
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
                    print ("FAIL mesh.tessface_uv_textures.active")
                    use_uv_coords = False
                    has_uv = False
                else:
                    active_uv_layer = active_uv_layer.data

            alpha_vertex_color = False
            if has_vcol:
                active_col_layer = mesh.tessface_vertex_colors[0]
                if 'alpha' in mesh.tessface_vertex_colors:
                    print("HAVE ALPHA")
                    has_uv
                    uses_alpha = True
                    alpha_vertex_color = mesh.tessface_vertex_colors['alpha'].data
                if not active_col_layer:
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
                vertices_str += "\n{0:.4f}, {1:.4f}, {2:.4f}, ".format(*tuple(mesh_verts[v[0]].co[:]))  # co
                if use_uv_coords:
                    vertices_str += "{0:.2f}, {1:.2f}, ".format(*tuple( [v[2][0], 1.0-v[2][1]] ))
                if use_colors:
                    vertices_str += "{0:.2f}, {1:.2f}, {2:.2f}, {3:.2f}, ".format(*tuple( v[3] )) # col
                
            for pf in ply_faces:
                if len(pf) == 3:
                    vertex_indices_str += "{0}, {1}, {2}, ".format(*tuple([pf[0]+object_count,pf[1]+object_count,pf[2]+object_count]))
                else:
                    vertex_indices_str += "{0}, {1}, {2}, {0}, {2}, {3}, ".format(*tuple([pf[0]+object_count,pf[1]+object_count,pf[2]+object_count,pf[3]+object_count]))
            object_count += len(ply_verts)

            if use_uv_coords:
                if obj.SAUSAGE_alpha_texture:
                    uses_alpha = True
                texture_objects += "\n\n{static_vertices:["+vertices_str[0:-2]+"],\n"+"static_indicies:["+vertex_indices_str[0:-2]+"],alpha:"+str(uses_alpha).lower()+"}, "
            else:
                vertex_objects += "\n\n{static_vertices:["+vertices_str[0:-2]+"],\n"+"static_indicies:["+vertex_indices_str[0:-2]+"],alpha:"+str(uses_alpha).lower()+"}, "













    


    fw("{"+camera_str+
        "vertex_objects:["+vertex_objects[0:-2]+"],\n\n"+
        "texture_objects:["+texture_objects[0:-2]+"],\n\n"+

        "wire_vertices:"+wire_vertices_str+",\n"+
        "wire_indicies:"+wire_indicies_str+",\n"+
        "collide_groups:["+collide_groups_str[0:-2]+"],\n"+
        "spawn_points:["+spawn_points_str +"]}")
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
         global_matrix=None
         ):

    scene = context.scene
    obj = context.selected_objects

    objects = []
    tex_objects = []
    wireframe_objects = []
    box2d_objects = []
    spawn_points = []

    for oo in context.selected_objects:
        if oo.SAUSAGE_visible_object:
            #if oo.type != 'CAMERA' and len(oo.data.uv_layers) > 0:
            #    tex_objects.append(oo)
            #else:
            objects.append(oo)
        if oo.SAUSAGE_wireframe_object:
            wireframe_objects.append(oo)
        if oo.SAUSAGE_physics_edges:
            box2d_objects.append(oo)
        if oo.SAUSAGE_spawn_point:
            spawn_points.append(oo)

    if global_matrix is None:
        from mathutils import Matrix
        global_matrix = Matrix()

    if bpy.ops.object.mode_set.poll():
        bpy.ops.object.mode_set(mode='OBJECT')





    ret = save_selected(filepath, objects, box2d_objects, wireframe_objects, spawn_points,
                    use_normals=use_normals,
                    use_uv_coords=use_uv_coords,
                    use_colors=use_colors,
                    global_matrix=global_matrix
                    )



    return ret
