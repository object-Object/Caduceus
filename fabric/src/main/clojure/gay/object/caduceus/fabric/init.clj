(ns gay.object.caduceus.fabric.init
  (:require [gay.object.caduceus.init :as caduceus.init]
            [gay.object.caduceus.registry :as registry]))

(defn- init-registry [registrar]
  (let [registry (registry/get-registry registrar)]
    (registry/init
      registrar
      (fn [id value]
        (net.minecraft.core.Registry/register
          ^net.minecraft.core.Registry registry
          ^net.minecraft.resources.ResourceLocation id
          ^Object value)))))

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
