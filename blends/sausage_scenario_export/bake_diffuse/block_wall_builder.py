# ***** BEGIN GPL LICENSE BLOCK *****
#
# This program is free software; you may redistribute it, and/or
# modify it, under the terms of the GNU General Public License
# as published by the Free Software Foundation - either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program. If not, write to:
#
#	the Free Software Foundation Inc.
#	51 Franklin Street, Fifth Floor
#	Boston, MA 02110-1301, USA
#
# or go online at: http://www.gnu.org/licenses/ to view license options.
#
# ***** END GPL LICENCE BLOCK *****

bl_info = {
    "name": "Block Wall Builder",
    "author": "Jonathon Anderson",
    "version": (0, 1),
    "blender": (2, 6, 3),
    "location": "View3D > Add > Mesh > Block Wall",
    "description": "Builder for block walls.",
    "warning": "Low Beta",
    "wiki_url": "",
    "tracker_url": "",
    "category": "Add Mesh"}

#Version History
#V0.1 2012/5/??  First release!


import bpy
from bpy.types import Operator
from bpy_extras.object_utils import AddObjectHelper, object_data_add
from mathutils import Vector


class Rectangle():
    """I am a rectangle that is not turned"""
    def __init__(self, position, extent):
        self.position = position
        self.extent = extent
        self.points = [position,
                       Vector((position.x + extent.x, position.y)),
                       position + extent,
                       Vector((position.x, position.y + extent.y))]

    def simple_overlaps(self, other):
        l, r = self.position.x, self.position.x + self.extent.x
        d, u = self.position.y, self.position.y + self.extent.y
        for p in other.points:
            if (l <= p.x) and (p.x <= r) and (d <= p.y) and (p.y <= u):
                return True
        return False

    def overlaps(self, other):
        return self.simple_overlaps(other) or other.simple_overlaps(self)


class Block():
    """I represent a single block in the wall"""
    def __init__(self, position=Vector((0, 0)), split2=False):
        self.position = position
        self.split2 = split2

    def test_for_hole(self, op, extent=None):
        if extent == None:
            if self.split2:
                extent = Vector((op.block.x / 2, op.block.y))
            else:
                extent = op.block
        if not self.split2 and op.split2:
            l = [0, 1]
            b1 = Block(self.position, True)
            l[0] = b1.test_for_hole(op)
            b2 = Block(Vector((self.position.x + (op.block.x / 2),
                               self.position.y)), True)
            l[1] = b2.test_for_hole(op)
            if l == [False, False]:
                return False
            elif l == [True, False]:
                return b1
            elif l == [False, True]:
                return b2
            elif l == [True, True]:
                return True
        grout = Vector.Fill(2, op.grout / 2)
        ext = extent - Vector.Fill(2, op.grout)
        pos = self.position + grout
        return not Rectangle(pos, ext).overlaps(Rectangle(op.holep, op.holes))

    def build(self, op, vadd):
        if self.split2:
            extent = Vector((op.block.x / 2, op.block.y))
        else:
            extent = op.block
        if op.holeb:
            t = self.test_for_hole(op, extent)
            if type(t) == Block:
                return t.build(op, vadd)
            elif t == False:
                return [], []
        grout = Vector.Fill(2, op.grout / 2)
        ext = extent - Vector.Fill(2, op.grout)
        pos = self.position + grout
        v = [0, 1, 2, 3, 4, 5, 6, 7]
        v[0] = (pos).to_3d()
        v[1] = Vector((pos.x, pos.y + ext.y, 0))
        v[2] = (pos + ext).to_3d()
        v[3] = Vector((pos.x + ext.x, pos.y, 0))
        v[4] = v[0] + Vector((0, 0, op.depth))
        v[5] = v[1] + Vector((0, 0, op.depth))
        v[6] = v[2] + Vector((0, 0, op.depth))
        v[7] = v[3] + Vector((0, 0, op.depth))
        f = [[0, 1, 2, 3], [4, 5, 6, 7],
             [0, 1, 5, 4], [1, 2, 6, 5],
             [2, 3, 7, 6], [3, 0, 4, 7]]
        for l in f:
            for i in range(len(l)):
                l[i] += vadd
        return v, f


class BlockRow():
    """I represent a row of blocks"""

    def __init__(self, position=Vector((0, 0))):
        self.position = position

    def build(self, op, offset, vadd):
        self.siz_y = op.block.y
        block_num = int(op.wall.x // op.block.x)
        verts = []
        faces = []
        pos = self.position

        if op.split2 and offset:
            v, f = Block(pos, True).build(op, vadd + len(verts))
            verts.extend(v)
            faces.extend(f)

        if offset:
            pos += Vector((op.block.x / 2.0, 0))
            block_num -= 1

        for i in range(0, block_num):
            v, f = Block(pos).build(op, vadd + len(verts))
            verts.extend(v)
            faces.extend(f)
            pos += Vector((op.block.x, 0))

        if op.split2 and offset:
            v, f = Block(pos, True).build(op, vadd + len(verts))
            verts.extend(v)
            faces.extend(f)

        return (verts, faces)


def add_object(self, context):
    row_num = self.wall.y // self.block.y
    verts = []
    edges = []
    faces = []

    offset = False

    for i in range(0, int(row_num)):
        row = BlockRow(Vector((0, i * self.block.y)))
        v, f = row.build(self, offset, len(verts))
        verts.extend(v)
        faces.extend(f)
        if self.offset:
            offset = not offset

    mesh = bpy.data.meshes.new(name="Block Wall")
    mesh.from_pydata(verts, edges, faces)
    # useful for development when the mesh may be invalid.
    # mesh.validate(verbose=True)
    object_data_add(context, mesh, operator=self)


class add_mesh_block_wall_build(Operator, AddObjectHelper):
    """Build a block wall"""
    bl_idname = "mesh.block_wall_build"
    bl_label = "Block Wall"
    bl_description = "Build a new block wall"
    bl_options = {'REGISTER', 'UNDO'}

    block = bpy.props.FloatVectorProperty(
                      name="Average block size",
                      default=(1.0, 1.0),
                      subtype='XYZ',
                      description="Average size of the blocks",
                      size=2
                      )

    offset = bpy.props.BoolProperty(
                       name="Offset",
                       description="Move each layer a little to the side",
                       default=True
                       )

    split2 = bpy.props.BoolProperty(
                       name="Split",
                       description="Allow half blocks",
                       default=True
                       )

    wall = bpy.props.FloatVectorProperty(
                     name="Wall size",
                     default=(3.0, 3.0),
                     size=2,
                     subtype='XYZ',
                     description="Size of the entire wall"
                     )

    depth = bpy.props.FloatProperty(
                      name="Depth",
                      default=0.5,
                      subtype='DISTANCE',
                      description="Thickness of the wall"
                      )

    grout = bpy.props.FloatProperty(
                      name="Grout",
                      default=0.25,
                      subtype='DISTANCE',
                      description="Space between the blocks of the wall"
                      )

    holeb = bpy.props.BoolProperty(
                      name="Hole",
                      description="Have a rectangular hole in the wall",
                      default=False
                      )

    holes = bpy.props.FloatVectorProperty(
                      name="Hole size",
                      default=(1, 1),
                      size=2,
                      subtype='XYZ',
                      description="Size of the hole"
                      )

    holep = bpy.props.FloatVectorProperty(
                      name="Hole position",
                      default=(1, 1),
                      size=2,
                      subtype='XYZ',
                      description="Position of the hole (lower left corner)"
                      )

    def draw(self, context):
        layout = self.layout
        col = layout.column()

        row = col.row()
        row.prop(self, 'split2')
        row.prop(self, 'offset')

        col.prop(self, 'block')
        col.prop(self, 'wall')
        col.prop(self, 'depth')
        col.prop(self, 'grout')
        col.prop(self, 'holeb')

        if self.holeb:
            col.prop(self, 'holes')
            col.prop(self, 'holep')

    def execute(self, context):
        if self.block.x > self.wall.x:
            return {'CANCELLED'}
        elif self.block.y > self.wall.y:
            return {'CANCELLED'}
        elif (self.grout > self.block.x) or (self.grout > self.block.y):
            return {'CANCELLED'}

        add_object(self, context)

        return {'FINISHED'}


# Registration

def add_object_button(self, context):
    self.layout.operator(
        add_mesh_block_wall_build.bl_idname,
        text=add_mesh_block_wall_build.bl_label,
        icon="MOD_BUILD")


def register():
    bpy.utils.register_class(add_mesh_block_wall_build)
    bpy.types.INFO_MT_mesh_add.append(add_object_button)


def unregister():
    bpy.utils.unregister_class(add_mesh_block_wall_build)
    bpy.types.INFO_MT_mesh_add.remove(add_object_button)


if __name__ == "__main__":
    register()