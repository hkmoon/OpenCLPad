import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.*;
import plugin.ModelPlugin;
import plugin.desginer.KernelDesigner;

public class SumExample extends ModelPlugin {

	@Override
	public String getName() {
		return "OpenCL";
	}

	@Override
	public String getAuthor() {
		return "HongKee Moon";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public void process()
	{
		try {
			doSumExample();
		} catch (org.lwjgl.LWJGLException ex)
		{
			System.out.println(ex);
		}
		System.out.println("Finished");
	}

	// OpenCL variables
	public static CLContext context;
	public static CLPlatform platform;
	public static List<CLDevice> devices;
	public static CLCommandQueue queue;

	public static void doSumExample() throws LWJGLException {
		// Create our OpenCL context to run commands
		initializeCL();
		// Create an OpenCL 'program' from a source code file
		// ! Don't change KernelDesigner.kernelSource which points out your kernel program !
		CLProgram sumProgram = CL10.clCreateProgramWithSource(context, KernelDesigner.kernelSource, null);
		// Build the OpenCL program, store it on the specified device
		int error = CL10.clBuildProgram(sumProgram, devices.get(0), "", null);
		// Check for any OpenCL errors
		Util.checkCLError(error);
		// Create a kernel instance of our OpenCl program
		CLKernel sumKernel = CL10.clCreateKernel(sumProgram, "sum", null);

		// Used to determine how many units of work to do
		final int size = 100;
		// Error buffer used to check for OpenCL error that occurred while a command was running
		IntBuffer errorBuff = BufferUtils.createIntBuffer(1);

		// Create our first array of numbers to add to a second array of numbers
		float[] tempData = new float[size];
		for(int i = 0; i < size; i++) {
			tempData[i] = i;
		}
		// Create a buffer containing our array of numbers, we can use the buffer to create an OpenCL memory object
		FloatBuffer aBuff = BufferUtils.createFloatBuffer(size);
		aBuff.put(tempData);
		aBuff.rewind();
		// Create an OpenCL memory object containing a copy of the data buffer
		CLMem aMemory = CL10.clCreateBuffer(context, CL10.CL_MEM_WRITE_ONLY | CL10.CL_MEM_COPY_HOST_PTR, aBuff, errorBuff);
		// Check if the error buffer now contains an error
		Util.checkCLError(errorBuff.get(0));

		// Create our second array of numbers
		for(int j = 0, i = size-1; j < size; j++, i--) {
			tempData[j] = i;
		}
		// Create a buffer containing our second array of numbers
		FloatBuffer bBuff = BufferUtils.createFloatBuffer(size);
		bBuff.put(tempData);
		bBuff.rewind();

		// Create an OpenCL memory object containing a copy of the data buffer
		CLMem bMemory = CL10.clCreateBuffer(context, CL10.CL_MEM_WRITE_ONLY | CL10.CL_MEM_COPY_HOST_PTR, bBuff, errorBuff);
		// Check if the error buffer now contains an error
		Util.checkCLError(errorBuff.get(0));

		// Create an empty OpenCL buffer to store the result of adding the numbers together
		CLMem resultMemory = CL10.clCreateBuffer(context, CL10.CL_MEM_READ_ONLY, size*4, errorBuff);
		// Check for any error creating the memory buffer
		Util.checkCLError(errorBuff.get(0));

		// Set the kernel parameters
		sumKernel.setArg(0, aMemory);
		sumKernel.setArg(1, bMemory);
		sumKernel.setArg(2, resultMemory);
		sumKernel.setArg(3, size);

		// Create a buffer of pointers defining the multi-dimensional size of the number of work units to execute
		final int dimensions = 1;
		PointerBuffer globalWorkSize = BufferUtils.createPointerBuffer(dimensions);
		globalWorkSize.put(0, size);
		// Run the specified number of work units using our OpenCL program kernel
		CL10.clEnqueueNDRangeKernel(queue, sumKernel, dimensions, null, globalWorkSize, null, null, null);
		CL10.clFinish(queue);

		//This reads the result memory buffer
		FloatBuffer resultBuff = BufferUtils.createFloatBuffer(size);
		// We read the buffer in blocking mode so that when the method returns we know that the result buffer is full
		CL10.clEnqueueReadBuffer(queue, resultMemory, CL10.CL_TRUE, 0, resultBuff, null, null);
		// Print the values in the result buffer
		for(int i = 0; i < resultBuff.capacity(); i++) {
			System.out.println("result at " + i + " = " + resultBuff.get(i));
		}
		// This should print out 100 lines of result floats, each being 99.

		// Destroy our kernel and program
		CL10.clReleaseKernel(sumKernel);
		CL10.clReleaseProgram(sumProgram);
		// Destroy our memory objects
		CL10.clReleaseMemObject(aMemory);
		CL10.clReleaseMemObject(bMemory);
		CL10.clReleaseMemObject(resultMemory);
		// Destroy the OpenCL context
		destroyCL();
	}


	public static void initializeCL() throws LWJGLException {
		IntBuffer errorBuf = BufferUtils.createIntBuffer(1);
		// Create OpenCL
		CL.create();
		// Get the first available platform
		platform = CLPlatform.getPlatforms().get(0);
		// Run our program on the GPU
		devices = platform.getDevices(CL10.CL_DEVICE_TYPE_GPU);
		// Create an OpenCL context, this is where we could create an OpenCL-OpenGL compatible context
		context = CLContext.create(platform, devices, errorBuf);
		// Create a command queue
		queue = CL10.clCreateCommandQueue(context, devices.get(0), CL10.CL_QUEUE_PROFILING_ENABLE, errorBuf);
		// Check for any errors
		Util.checkCLError(errorBuf.get(0));
	}


	public static void destroyCL() {
		// Finish destroying anything we created
		CL10.clReleaseCommandQueue(queue);
		CL10.clReleaseContext(context);
		// And release OpenCL, after this method call we cannot use OpenCL unless we re-initialize it
		CL.destroy();
	}
}