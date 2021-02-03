package engine;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import util.Time;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    int width, height;

    private final String title;

    private static Window window = null;

    private long windowID;

    private static Scene currentScene;

    public float r = 1, g = 1, b = 1, a = 1;


    private Window() {
        //1920 x 1080
        this.width = 1000;
        this.height = 600;
        this.title = "Game";
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false : "Unknown scene '" + newScene +"'";
                break;
        }
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return window;
    }

    public void run() {

        System.out.println("LWJGL Run: " + Version.getVersion());

        try {
            // initialize window
            init();
            // loop for window
            loop();
        } finally {
            // Free memory
            glfwFreeCallbacks(windowID);
            glfwDestroyWindow(windowID);

            // terminate GLFW and free error callback
            glfwTerminate();
            Objects.requireNonNull(glfwSetErrorCallback(null)).free();
        }
    }

    public void init() {
        // setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Init GLFW
        if (!glfwInit()){
            throw new IllegalStateException("Unable to initalize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create window
        windowID = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (windowID == NULL) {
            throw new IllegalStateException("failed to create GLFW window.");
        }

        // Setup mouse callbacks
        glfwSetCursorPosCallback(windowID, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(windowID, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(windowID, MouseListener::mouseScrollCallback);

        // Setup key callback
        glfwSetKeyCallback(windowID, KeyListener::keyCallback);


        // Make the OpenGL context current
        glfwMakeContextCurrent(windowID);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make window visable
        glfwShowWindow(windowID);

        // Allows the usage of bindings
        GL.createCapabilities();

        // Sets start scene
        changeScene(0);
    }

    public void loop() {

        float beginTime = Time.getTime();
        float endTime = Time.getTime();
        float dt = -1.0f;

        while (!glfwWindowShouldClose(windowID)) {
            // Poll events
            glfwPollEvents();

            // Set clear color
            glClearColor(r, g, b, a);
            // Use clear color
            glClear(GL_COLOR_BUFFER_BIT);

            if(dt >= 0)
                currentScene.update(dt);


            // Swaps buffers
            glfwSwapBuffers(windowID);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

}
