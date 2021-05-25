# Overview
This path tracer is a hobby project used to experiment with the math used by commercial path tracers. I didn't want this project to be complex or hard to implement, so the entire thing is written in plain Java with minimal imports.

# What it can do
This path tracer runs on the CPU and can take advantage of multi threading to accelerate render time. All of the meshes have bounding volume hierarchies to further decrease render time. So far, this path tracer can only render `.obj` files.

This nice picture of a teapot and a bunny was rendered in full HD in less than five seconds from compiling!

![Teapot and Bunny](example.png)