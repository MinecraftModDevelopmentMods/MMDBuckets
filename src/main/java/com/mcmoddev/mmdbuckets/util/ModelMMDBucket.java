package com.mcmoddev.mmdbuckets.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Quat4f;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mcmoddev.mmdbuckets.MMDBuckets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public final class ModelMMDBucket implements IModel {
    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(new ResourceLocation(MMDBuckets.MODID, "full_bucket"), "inventory");
    public static final ModelResourceLocation EMPTY_LOCATION = new ModelResourceLocation(new ResourceLocation(MMDBuckets.MODID, "bucket"), "inventory");

    // minimal Z offset to prevent depth-fighting
    private static final float NORTH_Z_COVER = 7.496f / 16f;
    private static final float SOUTH_Z_COVER = 8.504f / 16f;
    private static final float NORTH_Z_FLUID = 7.498f / 16f;
    private static final float SOUTH_Z_FLUID = 8.502f / 16f;

    public static final IModel MODEL = new ModelMMDBucket();

    @Nullable
    private final ResourceLocation baseLocation;
    @Nullable
    private final ResourceLocation liquidLocation;
    @Nullable
    private final ResourceLocation coverLocation;
    @Nullable
    private final Fluid fluid;
    private final boolean flipGas;

    public ModelMMDBucket() {
        this(new ResourceLocation(MMDBuckets.MODID, "items/bucket_base"),
                new ResourceLocation(MMDBuckets.MODID, "items/bucket_fluid"),
                new ResourceLocation(MMDBuckets.MODID, "items/bucket_cover"),
                null, false);
    }

    public ModelMMDBucket(@Nullable ResourceLocation baseLocation, @Nullable ResourceLocation liquidLocation, @Nullable ResourceLocation coverLocation, @Nullable Fluid fluid, boolean flipGas) {
        this.baseLocation = baseLocation;
        this.liquidLocation = liquidLocation;
        this.coverLocation = coverLocation;
        this.fluid = fluid;
        this.flipGas = flipGas;
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        if (baseLocation != null)
            builder.add(baseLocation);
        if (liquidLocation != null)
            builder.add(liquidLocation);
        if (coverLocation != null)
            builder.add(coverLocation);

        return builder.build();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        // if the fluid is a gas wi manipulate the initial state to be rotated 180? to turn it upside down
        if (flipGas && fluid != null && fluid.isGaseous()) {
            state = new ModelStateComposition(state, TRSRTransformation.blockCenterToCorner(new TRSRTransformation(null, new Quat4f(0, 0, 1, 0), null, null)));
        }

        TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());
        TextureAtlasSprite fluidSprite = null;
        TextureAtlasSprite particleSprite = null;
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        if (fluid != null) {
            fluidSprite = bakedTextureGetter.apply(fluid.getStill());
        }

        if (baseLocation != null) {
            // build base (insidest)
            IBakedModel model = (new ItemLayerModel(ImmutableList.of(baseLocation))).bake(state, format, bakedTextureGetter);
            builder.addAll(model.getQuads(null, null, 0).stream()
                    // early packing to preserve tint index
                    .map(x -> new BakedQuad(x.getVertexData(), 0, x.getFace(), x.getSprite(), x.shouldApplyDiffuseLighting(), x.getFormat()))
                    .iterator());
            particleSprite = model.getParticleTexture();
        }
        if (liquidLocation != null && fluidSprite != null) {
            TextureAtlasSprite liquid = bakedTextureGetter.apply(liquidLocation);
            // build liquid layer (inside)
            builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, NORTH_Z_FLUID, EnumFacing.NORTH, fluid.getColor()));
            builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, fluid.getColor()));
            particleSprite = fluidSprite;
        }
        if (coverLocation != null) {
            // cover (the actual item around the other two)
            TextureAtlasSprite cover = bakedTextureGetter.apply(coverLocation);
            builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, NORTH_Z_COVER, cover, EnumFacing.NORTH, 0xffffffff));
            builder.add(ItemTextureQuadConverter.genQuad(format, transform, 0, 0, 16, 16, SOUTH_Z_COVER, cover, EnumFacing.SOUTH, 0xffffffff));
            if (particleSprite == null) {
                particleSprite = cover;
            }
        }
        Iterator<BakedQuad> quads = builder.build().stream()
                .map(x -> (x instanceof UnpackedBakedQuad)
                        ? new BakedQuad(x.getVertexData(), 42, x.getFace(), x.getSprite(), x.shouldApplyDiffuseLighting(), x.getFormat())
                        : x)
                .iterator();

        Map<TransformType, TRSRTransformation> tMap = Maps.newHashMap();
        tMap.putAll(PerspectiveMapWrapper.getTransforms(ItemCameraTransforms.DEFAULT));
        tMap.putAll(PerspectiveMapWrapper.getTransforms(state));
        return new BakedMMDBucket(this, ImmutableList.copyOf(quads), particleSprite, format, ImmutableMap.copyOf(tMap), Maps.newHashMap());
    }

    /**
     * Sets the liquid in the model.
     * fluid - Name of the fluid in the FluidRegistry
     * flipGas - If "true" the model will be flipped upside down if the liquid is a gas. If "false" it wont
     * <p/>
     * If the fluid can't be found, water is used
     */
    @Override
    public ModelMMDBucket process(ImmutableMap<String, String> customData) {
        String fluidName = customData.get("fluid");
        Fluid fluid = FluidRegistry.getFluid(fluidName);

        if (fluid == null) fluid = this.fluid;

        boolean flip = flipGas;
        if (customData.containsKey("flipGas")) {
            String flipStr = customData.get("flipGas");
            if (flipStr.equals("true")) flip = true;
            else if (flipStr.equals("false")) flip = false;
            else
                throw new IllegalArgumentException(String.format("DynBucket custom data \"flipGas\" must have value \'true\' or \'false\' (was \'%s\')", flipStr));
        }

        // create new model with correct liquid
        return new ModelMMDBucket(baseLocation,
                liquidLocation,
                coverLocation,
                fluid, flip);
    }

    /**
     * Allows to use different textures for the model.
     * There are 3 layers:
     * base - The empty bucket/container
     * fluid - A texture representing the liquid portion. Non-transparent = liquid
     * cover - An overlay that's put over the liquid (optional)
     * <p/>
     * If no liquid is given a hardcoded variant for the bucket is used.
     */
    @Override
    public ModelMMDBucket retexture(ImmutableMap<String, String> textures) {
        ResourceLocation base = baseLocation;
        ResourceLocation liquid = liquidLocation;
        ResourceLocation cover = coverLocation;

        if (textures.containsKey("base"))
            base = new ResourceLocation(textures.get("base"));
        if (textures.containsKey("fluid"))
            liquid = new ResourceLocation(textures.get("fluid"));
        if (textures.containsKey("cover"))
            cover = new ResourceLocation(textures.get("cover"));

        return new ModelMMDBucket(base, liquid, cover, fluid, flipGas);
    }

    public enum LoaderMMDBucket implements ICustomModelLoader {
        INSTANCE;

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation.getResourceDomain().equals(MMDBuckets.MODID) && modelLocation.getResourcePath().contains("full_bucket");
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) {
            return MODEL;
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
            // no need to clear cache since we create a new model instance
        }

        public void register(TextureMap map) {
            ResourceLocation bucketCover = new ResourceLocation(MMDBuckets.MODID, "items/bucket_cover");
            BucketCoverSprite bucketCoverSprite = new BucketCoverSprite(bucketCover);
            map.setTextureEntry(bucketCoverSprite);

            ResourceLocation bucketBase = new ResourceLocation(MMDBuckets.MODID, "items/bucket_base");
            BucketBaseSprite bucketBaseSprite = new BucketBaseSprite(bucketBase);
            map.setTextureEntry(bucketBaseSprite);
        }
    }

    private static final class BucketBaseSprite extends TextureAtlasSprite {
        private final ResourceLocation bucket = new ResourceLocation(MMDBuckets.MODID, "items/bucket");
        private final ImmutableList<ResourceLocation> dependencies = ImmutableList.of(bucket);

        private BucketBaseSprite(ResourceLocation resourceLocation) {
            super(resourceLocation.toString());
        }

        @Override
        public boolean hasCustomLoader(@Nonnull IResourceManager manager, @Nonnull ResourceLocation location) {
            return true;
        }

        @Override
        public Collection<ResourceLocation> getDependencies() {
            return dependencies;
        }

        @Override
        public boolean load(@Nonnull IResourceManager manager, @Nonnull ResourceLocation location, @Nonnull Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
            final TextureAtlasSprite sprite = textureGetter.apply(bucket);
            width = sprite.getIconWidth();
            height = sprite.getIconHeight();
            final int[][] pixels = sprite.getFrameTextureData(0);
            this.clearFramesTextureData();
            this.framesTextureData.add(pixels);
            return false;
        }
    }

    /**
     * Creates a bucket cover sprite from the vanilla resource.
     */
    private static final class BucketCoverSprite extends TextureAtlasSprite {
        private final ResourceLocation bucket = new ResourceLocation(MMDBuckets.MODID, "items/bucket_fluid");
        private final ResourceLocation bucketCoverMask = new ResourceLocation(MMDBuckets.MODID, "items/bucket_fluid_mask");
        private final ImmutableList<ResourceLocation> dependencies = ImmutableList.of(bucket, bucketCoverMask);

        private BucketCoverSprite(ResourceLocation resourceLocation) {
            super(resourceLocation.toString());
        }

        @Override
        public boolean hasCustomLoader(@Nonnull IResourceManager manager, @Nonnull ResourceLocation location) {
            return true;
        }

        @Override
        public Collection<ResourceLocation> getDependencies() {
            return dependencies;
        }

        @Override
        public boolean load(@Nonnull IResourceManager manager, @Nonnull ResourceLocation location, @Nonnull Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
            final TextureAtlasSprite sprite = textureGetter.apply(bucket);
            final TextureAtlasSprite alphaMask = textureGetter.apply(bucketCoverMask);
            width = sprite.getIconWidth();
            height = sprite.getIconHeight();
            final int[][] pixels = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
            pixels[0] = new int[width * height];

            IResource empty = getResource(new ResourceLocation(MMDBuckets.MODID, "textures/items/bucket_fluid.png"));
            IResource mask = getResource(new ResourceLocation(MMDBuckets.MODID, "textures/items/bucket_fluid_mask.png"));

            // use the alpha mask if it fits, otherwise leave the cover texture blank
            if (empty != null && mask != null && Objects.equals(empty.getResourcePackName(), mask.getResourcePackName()) &&
                    alphaMask.getIconWidth() == width && alphaMask.getIconHeight() == height) {
                final int[][] oldPixels = sprite.getFrameTextureData(0);
                final int[][] alphaPixels = alphaMask.getFrameTextureData(0);

                for (int p = 0; p < width * height; p++) {
                    final int alphaMultiplier = alphaPixels[0][p] >>> 24;
                    final int oldPixel = oldPixels[0][p];
                    final int oldPixelAlpha = oldPixel >>> 24;
                    final int newAlpha = oldPixelAlpha * alphaMultiplier / 0xFF;
                    pixels[0][p] = (oldPixel & 0xFFFFFF) + (newAlpha << 24);
                }
            }

            this.clearFramesTextureData();
            this.framesTextureData.add(pixels);
            return false;
        }

        @Nullable
        private static IResource getResource(ResourceLocation resourceLocation) {
            try {
                return Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);
            } catch (IOException ignored) {
                return null;
            }
        }
    }

    private static final class BakedMMDBucketOverrideHandler extends ItemOverrideList {
        public static final BakedMMDBucketOverrideHandler INSTANCE = new BakedMMDBucketOverrideHandler();

        private BakedMMDBucketOverrideHandler() {
            super(ImmutableList.of());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            FluidStack fluidStack = FluidUtil.getFluidContained(stack);

            // not a fluid item apparently
            if (fluidStack == null) {
                // empty bucket
                return originalModel;
            }

            BakedMMDBucket model = (BakedMMDBucket) originalModel;

            Fluid fluid = fluidStack.getFluid();
            String name = fluid.getName();

            if (!model.cache.containsKey(name)) {
                IModel parent = model.parent.process(ImmutableMap.of("fluid", name));
                Function<ResourceLocation, TextureAtlasSprite> textureGetter;
                textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());

                IBakedModel bakedModel = parent.bake(new SimpleModelState(model.getTransforms()), model.format, textureGetter);
                model.cache.put(name, bakedModel);
                return bakedModel;
            }

            return model.cache.get(name);
        }
    }

    // the dynamic bucket is based on the empty bucket
    private static final class BakedMMDBucket extends BakedItemModel {
        private final ModelMMDBucket parent;
        private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
        private final VertexFormat format;

        public BakedMMDBucket(ModelMMDBucket parent,
                              ImmutableList<BakedQuad> quads,
                              TextureAtlasSprite particle,
                              VertexFormat format,
                              ImmutableMap<TransformType, TRSRTransformation> transforms,
                              Map<String, IBakedModel> cache) {
            super(quads, particle, transforms, BakedMMDBucketOverrideHandler.INSTANCE);
            this.format = format;
            this.parent = parent;
            this.cache = cache;
        }

        public ImmutableMap<TransformType, TRSRTransformation> getTransforms() {
            return this.transforms;
        }
    }
}