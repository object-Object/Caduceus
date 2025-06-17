(ns gay.object.caduceus.forge.ForgeCaduceus
  (:import (dev.architectury.platform.forge EventBuses)
           (gay.object.caduceus Caduceus CaduceusClient)
           (net.minecraftforge.eventbus.api EventPriority)
           (net.minecraftforge.fml.event.lifecycle FMLClientSetupEvent)
           (net.minecraftforge.fml.javafmlmod FMLJavaModLoadingContext)))

(defn init-client [^FMLClientSetupEvent _]
  (CaduceusClient/init))

(gen-class
  :name ^{net.minecraftforge.fml.common.Mod "caduceus"}
        gay.object.caduceus.forge.ForgeCaduceus
  :prefix ForgeCaduceus-
  :init init)

(defn ForgeCaduceus-init []
  (let [bus (.getModEventBus (FMLJavaModLoadingContext/get))]
    (EventBuses/registerModEventBus Caduceus/MODID bus)
    (.addListener bus EventPriority/NORMAL false FMLClientSetupEvent init-client))
  (Caduceus/init)
  [[] nil])
