# MT RAY-TRACER
This is a CPU Ray Tracer written in Java as a side project.
Through this project, I'm attempting to create an application that behaves like a real time renderer but runs entirely on the CPU so the rendering resolution is gonna be low.

## Working
__Here's how it works:__ <br />
The skeleton of this project is completely adapted from Peter Shirley's Ray Tracing in One Weekend. But instead of rendering everything on a single core, I've added multithreading to it to utilize the CPU as much as possible for better performance. <br />The output image is divided into a certain number of "chunks" (16 for now) which are assigned to all the threads. <br />
I'm working with an i5-12450H. It has 8 cores and 12 threads. So I'll explain it in the context of my CPU. <br />
So the screen is divided into 16 chunks. Let's say 12 of the chunks are assigned to all the threads at first. One for each. And when one thread is free to move on to the next part, it starts working on rendering the 13th part, then the next free thread moves on to another one of the remaining parts and so on... <br />The performance is good enough to run it at 800x600 resolution without any major noticeable performance degradation while moving. <br />While moving, the multisampling is turned down. From 50 samples per pixel to 1 sample per pixel so the image quality while moving is trash but when you stop moving, the samples per pixel go back up to 50 and the image becomes significantly better in terms of quality.