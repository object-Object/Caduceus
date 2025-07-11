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

Avoid using `:import` from remapped packages (ie. `net.minecraft`), since Clojure's implementation of `:import` results in loading classes by name at runtime.

Use fully-qualified type hints where necessary to ensure reflection is not used on Minecraft classes.

## Attribution

- Icon: SamsTheNerd ([GitHub](https://github.com/SamsTheNerd), [Modrinth](https://modrinth.com/user/SamsTheNerd))
