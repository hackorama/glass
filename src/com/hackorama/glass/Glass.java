package com.hackorama.glass;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.JWindow;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * Java3D Psuedo Transparent Window Hack
 * 
 * @author Kishan Thomas <kishan@hackorama.com> 2004/2005
 * 
 */
public class Glass {

    private Rectangle baseRectangle;
    private Robot robot;
    private BufferedImage baseImageBuffer;
    private static final String J3DURL = "http://java.sun.com/products/java-media/3D";
    private static final int WIDTH = 600;
    private static final int HEIGHT = 200;

    public Glass() {
        try {
            robot = new Robot();
        } catch (Exception e) {
            exitOnError(e, "Failed to create the Robot");
        }
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        baseRectangle = new Rectangle(screen.width / 2 - WIDTH / 2, screen.height / 2 - HEIGHT / 2, WIDTH, HEIGHT);
        baseImageBuffer = robot.createScreenCapture(baseRectangle);
        GlassWindow glassWindow = null;
        try {
            glassWindow = new GlassWindow();
        } catch (Exception e) {
            exitOnError(e, "Please Instal Java3D from " + J3DURL);
        }
        glassWindow.setVisible(true);
        glassWindow.setBounds(screen.width / 2 - WIDTH / 2, screen.height / 2 - HEIGHT / 2, WIDTH, HEIGHT);
    }

    private void exitOnError(Exception e, String msg) {
        System.out.println("Glass Error: " + msg);
        System.out.println(e);
        System.exit(1);
    }

    class GlassWindow extends JWindow {
        private static final long serialVersionUID = 1L;
    
        public GlassWindow() {
            Canvas3D mycanvas = null;
            try {
                mycanvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
            } catch (Exception e) {
                exitOnError(e, "Looks like you dont have a 3D capable display");
            }
            getContentPane().add(mycanvas, BorderLayout.CENTER);
            SimpleUniverse universe = new SimpleUniverse(mycanvas);
            universe.getViewingPlatform().setNominalViewingTransform();
            universe.addBranchGraph(createSceneGraph());
        }
    
        public BranchGroup createSceneGraph() {
            BranchGroup rootGroup = new BranchGroup();
    
            TransformGroup objScale = new TransformGroup();
            Transform3D transform3D = new Transform3D();
            transform3D.setScale(0.4);
            objScale.setTransform(transform3D);
            rootGroup.addChild(objScale);
    
            TransformGroup objTrans = new TransformGroup();
            objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            objScale.addChild(objTrans);
            Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
    
            Color3f fontColor = new Color3f(1.0f, 0.2f, 0.4f);
            ColoringAttributes coloringAttribs = new ColoringAttributes();
            coloringAttribs.setColor(fontColor);
    
            Appearance fontAppearance = new Appearance();
            Material material = new Material();
            material.setLightingEnable(true);
            fontAppearance.setMaterial(material);
            fontAppearance.setColoringAttributes(coloringAttribs);
    
            Appearance appearance = new Appearance();
            TransparencyAttributes transparencyAttribs = new TransparencyAttributes();
            transparencyAttribs.setTransparencyMode(TransparencyAttributes.BLENDED);
            transparencyAttribs.setTransparency(0.5f);
            appearance.setTransparencyAttributes(transparencyAttribs);
    
            PolygonAttributes polyAttribs = new PolygonAttributes();
            polyAttribs.setCullFace(PolygonAttributes.CULL_NONE);
            appearance.setPolygonAttributes(polyAttribs);
    
            Color3f objColor = new Color3f(0.7f, 0.8f, 1.0f);
            appearance.setMaterial(new Material(objColor, black, objColor, black, 1.0f));
    
            ColorCube colorCube = new ColorCube(0.8);
            colorCube.setAppearance(appearance);
            objTrans.addChild(colorCube);
    
            Font3D font3D;
            double tessellation = 0.0;
            if (tessellation > 0.0) {
                font3D = new Font3D(new Font("TestFont", Font.PLAIN, 1), tessellation, new FontExtrusion());
            } else {
                font3D = new Font3D(new Font("TestFont", Font.PLAIN, 1), new FontExtrusion());
            }
            Text3D text3D = new Text3D(font3D, "hackorama", new Point3f(-2.2f, -0.35f, 0.0f));
            Shape3D shape3D = new Shape3D();
            shape3D.setGeometry(text3D);
            shape3D.setAppearance(fontAppearance);
    
            objTrans.addChild(shape3D);
    
            Transform3D yAxis = new Transform3D();
            Alpha rotationAlpha = new Alpha(-1, 4000);
    
            RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, objTrans, yAxis, 0.0f,
                    (float) Math.PI * 2.0f);
            BoundingSphere boundingSphere = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
            rotator.setSchedulingBounds(boundingSphere);
            objTrans.addChild(rotator);
    
            TextureLoader textureLoader = new TextureLoader(baseImageBuffer);
            Background background = new Background(textureLoader.getImage());
            background.setApplicationBounds(boundingSphere);
            rootGroup.addChild(background);
    
            Color3f ambientColor = new Color3f(0.3f, 0.3f, 0.3f);
            AmbientLight ambientLightNode = new AmbientLight(ambientColor);
            ambientLightNode.setInfluencingBounds(boundingSphere);
            rootGroup.addChild(ambientLightNode);
    
            Color3f colorOne = new Color3f(1.0f, 1.0f, 0.9f);
            Vector3f directionOne = new Vector3f(1.0f, 1.0f, 1.0f);
            Color3f colorTwo = new Color3f(1.0f, 1.0f, 0.9f);
            Vector3f directionTwo = new Vector3f(-1.0f, -1.0f, -1.0f);
    
            DirectionalLight lightOne = new DirectionalLight(colorOne, directionOne);
            lightOne.setInfluencingBounds(boundingSphere);
            rootGroup.addChild(lightOne);
    
            DirectionalLight lightTwo = new DirectionalLight(colorTwo, directionTwo);
            lightTwo.setInfluencingBounds(boundingSphere);
            rootGroup.addChild(lightTwo);
    
            rootGroup.compile();
            return rootGroup;
        }
    
        @Override
        public void paint(Graphics g) {
        }
    
        @Override
        public void update(Graphics g) {
            baseImageBuffer = robot.createScreenCapture(baseRectangle);
        }
    }

    public static void main(String args[]) {
        System.out.println("Glass - Psuedo Transparent 3D Window ");
        new Glass();
    }
}
