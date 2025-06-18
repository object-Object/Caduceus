(ns gay.object.caduceus.fabric.init
  (:require [gay.object.caduceus.init :as caduceus.init]
            [gay.object.caduceus.registry :as registry])
  (:import (net.minecraft.core Registry)
           (net.minecraft.resources ResourceLocation)))

(defn- init-registry [registrar]
  (let [registry (registry/get-registry registrar)]
    (registry/init
      registrar
      (fn [id value]
        (^[Registry ResourceLocation Object]
          Registry/register registry id value)))))

(gen-class
  :name gay.object.caduceus.fabric.FabricCaduceus
  :implements [net.fabricmc.api.ModInitializer]
  :prefix FabricCaduceus-)

(defn FabricCaduceus-onInitialize [_]
  (caduceus.init/init init-registry))

(gen-class
  :name gay.object.caduceus.fabric.FabricCaduceusClient
  :implements [net.fabricmc.api.ClientModInitializer]
  :prefix FabricCaduceusClient-)

(defn FabricCaduceusClient-onInitializeClient [_]
  (caduceus.init/init-client))
