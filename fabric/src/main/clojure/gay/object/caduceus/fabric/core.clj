(ns gay.object.caduceus.fabric.core
  (:import (gay.object.caduceus Caduceus CaduceusClient)))

(gen-class
  :name gay.object.caduceus.fabric.FabricCaduceus
  :implements [net.fabricmc.api.ModInitializer]
  :prefix FabricCaduceus-)

(defn FabricCaduceus-onInitialize [_]
  (Caduceus/init))

(gen-class
  :name gay.object.caduceus.fabric.FabricCaduceusClient
  :implements [net.fabricmc.api.ClientModInitializer]
  :prefix FabricCaduceusClient-)

(defn FabricCaduceusClient-onInitializeClient [_]
  (CaduceusClient/init))
