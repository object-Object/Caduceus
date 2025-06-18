(ns gay.object.caduceus.core
  (:import (org.apache.logging.log4j LogManager)))

(def ^:const MODID "caduceus")

(def LOGGER (LogManager/getLogger MODID))

(defn id [path]
  (net.minecraft.resources.ResourceLocation/new MODID path))
