# Caduceus

[![powered by hexdoc](https://img.shields.io/endpoint?url=https://hexxy.media/api/v0/badge/hexdoc?label=1)](https://github.com/hexdoc-dev/hexdoc)

[CurseForge](https://curseforge.com/minecraft/mc-mods/caduceus) | [Modrinth](https://modrinth.com/mod/caduceus)

A Clojure-based Hex Casting addon for advanced meta-evaluation. Created for HexJam 2025.

## Overview

- Adds the following meta-evaluation features:
  - Delimited continuations
  - Continuation marks
  - Jump iota manipulation
  - Improved jump iota display
- All non-mixin code is written in Clojure.
- On Fabric, support is automatically added for third-party continuation frame types by scanning the classpath and generating a mixin at runtime with a dynamic `targets` list.

## Developing

IMPORTANT: Type hints for Minecraft classes should be **avoided** whenever possible. Clojure seems to compile them into code like `RT.classForName("net.minecraft.resources.ResourceLocation`, which fails on Fabric because it isn't remapped.

I think this should be fixable using [elide-meta](https://clojure.org/reference/compilation#_compiler_options), but Clojurephant's elideMeta option doesn't seem to be working.

Additionally, avoid using `:import` from remapped packages (ie. `net.minecraft`), since Clojure's implementation of `:import` also results in loading classes by name at runtime.

## Attribution

- Icon: `:irissy:` by hakimen.
