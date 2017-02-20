package net.bdew.covers.block

import java.util
import java.util.Collections

import mcmultipart.api.container.IPartInfo
import mcmultipart.api.multipart.{IMultipart, IMultipartTile}
import mcmultipart.api.slot.IPartSlot
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.util.{BlockRenderLayer, EnumFacing}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.property.IExtendedBlockState

import scala.collection.JavaConverters._

object MultipartCover extends IMultipart {
  override def getBlock: Block = BlockCover

  override def convertToMultipartTile(tileEntity: TileEntity): IMultipartTile =
    tileEntity match {
      case x: TileCover => x.multipart
      case _ => null
    }

  override def getSlotFromWorld(world: IBlockAccess, pos: BlockPos, state: IBlockState): IPartSlot = {
    BlockCover.getData(world, pos).map(_.slot).orNull
  }

  override def getSlotForPlacement(world: World, pos: BlockPos, state: IBlockState, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, placer: EntityLivingBase): IPartSlot =
    state.asInstanceOf[IExtendedBlockState].getValue(CoverInfoProperty).slot

  def getData(part: IPartInfo) = {
    if (part.getTile.getTileEntity.isInstanceOf[TileCover])
      Option(part.getTile.getTileEntity.asInstanceOf[TileCover].data)
    else
      None
  }

  override def canRenderInLayer(world: IBlockAccess, pos: BlockPos, part: IPartInfo, state: IBlockState, layer: BlockRenderLayer): Boolean =
    getData(part).exists(d => d.material.canRenderInLayer(d.material.getDefaultState, layer))

  override def getGhostSlots(part: IPartInfo): util.Set[IPartSlot] =
    getData(part).map(d=> d.shape.getSlotMask(d.slot, d.size)).getOrElse(Collections.emptySet())

  override def getOcclusionBoxes(part: IPartInfo): util.List[AxisAlignedBB] =
    getData(part).map(d => (d.shape.getPartBoxes(d.slot, d.size): List[AxisAlignedBB]).asJava).getOrElse(Collections.emptyList())
}
