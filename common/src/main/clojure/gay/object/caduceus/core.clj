(ns gay.object.caduceus.core
  (:import (net.minecraft.resources ResourceLocation)
           (org.apache.logging.log4j LogManager Logger)))

(def ^:const MODID "caduceus")

(def ^Logger LOGGER (LogManager/getLogger MODID))

(defn ^ResourceLocation id [path]
  (ResourceLocation/new MODID path))
