(ns gay.object.caduceus.forge.init
  (:require [gay.object.caduceus.core :as caduceus]
            [gay.object.caduceus.registry :as registry]
            [gay.object.caduceus.init :as caduceus.init])
  (:import (dev.architectury.platform.forge EventBuses)
           (net.minecraft.resources ResourceLocation)
           (net.minecraftforge.eventbus.api EventPriority IEventBus)
           (net.minecraftforge.fml.event.lifecycle FMLClientSetupEvent)
           (net.minecraftforge.fml.javafmlmod FMLJavaModLoadingContext)
           (net.minecraftforge.registries RegisterEvent RegisterEvent$RegisterHelper)))

(defn- add-listener [^IEventBus bus event-type consumer]
  (.addListener bus EventPriority/NORMAL false event-type consumer))

(defn- init-registry [bus]
  (fn [registrar]
    (add-listener
      bus
      RegisterEvent
      (fn [^RegisterEvent event]
        (.register
          event
          (registry/get-registry-key registrar)
          (fn [^RegisterEvent$RegisterHelper helper]
            (registry/init
              registrar
              (fn [^ResourceLocation id value]
                (.register helper id value)))))))))

(defn init-client [^FMLClientSetupEvent _event]
  (caduceus.init/init-client))

(gen-class
  :name ^{net.minecraftforge.fml.common.Mod "caduceus"} ; this doesn't compile with caduceus/MODID (???)
        gay.object.caduceus.forge.ForgeCaduceus
  :prefix ForgeCaduceus-
  :init init)

(defn ForgeCaduceus-init []
  (let [bus (.getModEventBus (FMLJavaModLoadingContext/get))]
    (EventBuses/registerModEventBus caduceus/MODID bus)
    (add-listener bus FMLClientSetupEvent init-client)
    (caduceus.init/init (init-registry bus)))
  [[] nil])
