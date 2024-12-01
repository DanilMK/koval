# General
The mod adds a new mechanic for assembling tools from various parts.

## Assembly Table
The main block that allows you to assemble and disassemble tools.
Craft: [flint, flint, crafting table, log]

## Mining Tool
The first tool in the mod, which is an analogue of vanilla pickaxes and others. As vanilla tools, you can enchant and rename it. To assemble it, you need parts from the following shapes: head and handle.

<details>
<summary>Available head shapes</summary>

- pickaxe
- axe
- shovel
- hoe


</details>


<details>
<summary>Available head materials</summary>

- wood
- stone
- iron
- gold
- diamonds
- netherite

</details>


Head parts are crafted on a workbench like vanilla tools, but flint is used instead of sticks. Netherite is made from diamonds on a smiting table.

<details>
<summary>Available handles </summary>

- stick 
- bone 
- stone rod 
- iron rod 
- gold rod 

</details>


Rods are made from the corresponding materials according to the recipe for sticks on the workbench.

# Configs and datapacks
All shapes and materials are configured via the datapack. There are two sections for this: `data/namespace/forging/materials` and `/forging/shapes`.
A material contains a set of properties and parameters that can be used for any parts.
Shapes contain possible tools, assembly conditions, part IDs and their own properties and parameters used by the part.
Any object can be used as a part by specifying it for the corresponding material in the shape file.

# Other
The mod was inspired by such mods as TConstract, Matter overdrive and Modular power suit. Many thanks to their authors. I will develop the Mod in this direction.
