# Overview
This path tracer is a hobby project used to experiment with the math used by commercial path tracers. I didn't want this project to be complex or hard to implement, so the entire thing is written in plain Java with minimal imports. However, that doesn't mean it's slow!

# What it can do
This path tracer runs on the CPU and can take advantage of multi threading to accelerate render time. All of the meshes have bounding volume hierarchies to further decrease render time. So far, this path tracer can render `.obj` files or custom shapes that have been hard-coded.

This path tracer supports any resolution, and can display the image as it's rendering.

This nice image of a teapot and a bunny was rendered in full HD in less than five seconds from compiling!

![Teapot and Bunny](img/example.png)

# Run it yourself
This project is light weight so running it yourself is simple.

## Installation 
1. Install a JDK on Windows
2. Download or clone the repository

## Using it
1. Edit the `run.bat` file to change the render settings
2. Run the `run.bat` file

