package com.ujr.oauth.gui;


import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ToggleSwitch extends Parent {

        private BooleanProperty switchedOn = new SimpleBooleanProperty(false);

        Duration duration = Duration.seconds(0.25);
        private TranslateTransition translateAnimation = new TranslateTransition(duration);
        private FillTransition fillAnimation = new FillTransition(duration);
        private FadeTransition fadeTransitionTextOFF = new FadeTransition(duration);
        private FadeTransition fadeTransitionTextON = new FadeTransition(duration);

        private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation, fadeTransitionTextOFF, fadeTransitionTextON);

        public BooleanProperty switchedOnProperty() {
            return switchedOn;
        }
        
        
        public boolean isSwitchedOn() {
        	return this.switchedOn.getValue();
        }
        
        private int size = 48;
        private int arcRetangle = 0;
        private boolean isTriggerCircle;

        public ToggleSwitch() {
        	arcRetangle = isTriggerCircle ? 0 : arcRetangle;
        	
            Rectangle background = new Rectangle(size + (size * 0.4), size / 2);
            background.setArcWidth( (size / 2) - arcRetangle);
            background.setArcHeight( (size / 2) - arcRetangle);
            background.setFill(Color.LIGHTGRAY);
            background.setStroke(Color.LIGHTGRAY);

            int sizeTrigger = (size / 2) + 0;
            Rectangle trigger = new Rectangle(sizeTrigger, sizeTrigger);
            trigger.setArcWidth( (size / 2) - arcRetangle);
            trigger.setArcHeight( (size / 2) - arcRetangle);
            trigger.setFill(Color.WHITE);
            trigger.setStroke(Color.LIGHTGRAY);
            
            RadialGradient radial = new RadialGradient(0,0,10,7,20,false,CycleMethod.NO_CYCLE,
            		new Stop(0.1, Color.valueOf("#cdcece")),
            		new Stop(0.6, Color.valueOf("#090a0c"))
            );
            trigger.setFill(radial);
            
            InnerShadow shade = new InnerShadow();
            shade.setWidth(30);
            shade.setHeight(30);
            shade.setOffsetX(-3);
            shade.setOffsetY(3.8);
            background.setEffect(shade);

            DropShadow shadow = new DropShadow();
            shadow.setRadius(2);
            shadow.setOffsetX(1.8);
            shadow.setOffsetY(1.8);
            trigger.setEffect(shadow);
            
            double textsOffSetY = 16.8;
            
            DropShadow shadowTextOFF = new DropShadow();
            shadowTextOFF.setOffsetX(2.8);
            shadowTextOFF.setOffsetY(2.8);
            shadowTextOFF.setRadius(0.5);
            shadowTextOFF.setColor(Color.valueOf("#C7C7C7"));
            Text textOFF = new Text("OFF");
            textOFF.setTranslateY(trigger.getTranslateY() + textsOffSetY);
            textOFF.setTranslateX(trigger.getTranslateX() + 30.5);
            textOFF.setFont(Font.font("Consola",FontWeight.EXTRA_BOLD, 12d));
            textOFF.setSmooth(true);
            textOFF.setFill(Color.valueOf("#656565"));
            textOFF.setEffect(shadowTextOFF);
            textOFF.setStroke(Color.valueOf("#656565"));
            textOFF.setStrokeType(StrokeType.CENTERED);
            textOFF.setStrokeWidth(0.1);
            
            DropShadow shadowTextON = new DropShadow();
            shadowTextON.setOffsetX(2.8);
            shadowTextON.setOffsetY(2.8);
            shadowTextON.setRadius(0.5);
            shadowTextON.setColor(Color.valueOf("#202020"));
            Text textON = new Text("ON");
            textON.setTranslateY(trigger.getTranslateY() + textsOffSetY);
            textON.setTranslateX(trigger.getTranslateX() + 12);
            textON.setFont(Font.font("Consola",FontWeight.EXTRA_BOLD, 12d));
            textON.setSmooth(true);
            textON.setFill(Color.valueOf("#FFFFFF"));
            textON.setEffect(shadowTextON);
            textON.setStroke(Color.valueOf("#656565"));
            textON.setStrokeType(StrokeType.CENTERED);
            textON.setStrokeWidth(0.1);
            textON.setOpacity(0.0);
            
            
            fadeTransitionTextOFF.setNode(textOFF);
            fadeTransitionTextON.setNode(textON);
            translateAnimation.setNode(trigger);
            fillAnimation.setShape(background);

            getChildren().addAll(background, trigger, textOFF, textON);

            switchedOn.addListener((obs, oldState, newState) -> {
                boolean isOn = newState.booleanValue();
                translateAnimation.setToX(isOn ? size - (size * 0.1) : 0);
                fillAnimation.setFromValue(isOn ? Color.LIGHTGRAY : Color.DARKSLATEGRAY);
                fillAnimation.setToValue(isOn ? Color.DARKSLATEGRAY : Color.LIGHTGRAY);
                fadeTransitionTextOFF.setFromValue(isOn ? 1.0 : 0.0);
                fadeTransitionTextOFF.setToValue(isOn ? 0.0 : 1.0);
                fadeTransitionTextON.setFromValue(isOn ? 0.0 : 1.0);
                fadeTransitionTextON.setToValue(isOn ? 1.0 : 0.0);
                animation.play();
            });

            setOnMouseClicked(event -> {
                switchedOn.set(!switchedOn.get());
            });
            
            setCursor(Cursor.HAND);
        }
    }
