![Teapot and Bunny](example.png)

# Overview
This path tracer is a hobby project used to experiment with the math used by commercial path tracers. I didn't want this project to be complex or hard to implement, so the entire thing is written in plain Java with minimal imports. However, that doesn't mean it's slow!

# What it can do
This path tracer runs on the CPU and can take advantage of multi threading to accelerate render time. All of the meshes have bounding volume hierarchies to further decrease render time. So far, this path tracer can render `.obj` files, but I may add support for more file types in the future.

This path tracer supports any resolution, and can display the image as it's rendering.

# Run it yourself
This project is light-weight so running it yourself is easy. The entire thing is compiled and run through a small batch script. Using batch does limit users to Windows, but that's the price we pay for ease of use.

 - Install JDK on Windows
 - Clone this repository
 - Edit the `run.bat` file to change the render and directory settings

The render setting are specified by the `args` variable. For example, if I would like to render the image in 1080p with a real-time display I would have the following:

```bat
:: run.bat
SET args=1920 1080 -v
```

The first two numbers specify the resolution, and `-v` tells the path-tracer to create the realtime display.

The full list of rendering setting are as follows:

| flag | desc |
|---|---|
| **-m int** | Enable multithreading and set pixel dimensions of the chunks |
| **-a int** | Set the sub-pixel sampling (antialiasing) resolution. Supplying an argument of `2`, for example will sample the pixel `2*2=4` times in a grid-like fashion. |
| **-o** | Enable `.png` file output on completion |
| **-d** | Enable realtime image Display |
| **-v** | Verbose console output |


