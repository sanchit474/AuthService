import { assets as baseAssets } from "./asset";

// Provide a compatibility module so components importing
// ../assets/assets.js or ../assets/assets can work.

export const assets = {
  ...baseAssets,
  // Provide `header` fallback in case components expect it
  header: baseAssets.logo,
};

export default assets;
