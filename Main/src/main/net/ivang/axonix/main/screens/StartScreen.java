/*
 * Copyright 2012-2013 Ivan Gadzhega
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.ivang.axonix.main.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.tablelayout.Cell;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.AxonixGame;
import net.ivang.axonix.main.events.intents.BackIntent;
import net.ivang.axonix.main.events.intents.DefaultIntent;
import net.ivang.axonix.main.events.intents.screen.LevelsScreenIntent;
import net.ivang.axonix.main.events.intents.screen.OptionsScreenIntent;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class StartScreen extends BaseScreen {

    private Button startButton;
    private Button optionsButton;
    private Cell logoCell;
    private Cell startButtonCell;
    private Cell optionsButtonCell;

    @Inject
    private StartScreen(AxonixGame game, InputMultiplexer inputMultiplexer, final EventBus eventBus) {
        super(game, inputMultiplexer, eventBus);
        // root table
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        // logo
        Image logo = new Image(skin, "logo");
        logoCell = rootTable.add(logo);
        rootTable.row();
        // start button
        Style style = getStyleByHeight();
        startButton = new TextButton("Start", skin, style.toString());
        startButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                eventBus.post(new LevelsScreenIntent());
            }
        });
        startButtonCell = rootTable.add(startButton);
        rootTable.row();
        // options button
        optionsButton = new TextButton("Options", skin, style.toString());
        optionsButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                eventBus.post(new OptionsScreenIntent());
            }
        });
        optionsButtonCell = rootTable.add(optionsButton);
        // stage
        stage.addActor(rootTable);
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, true);
        Style style = getStyleByHeight(height);
        float scale = getScaleByStyle(style);
        resizeLogo(scale);
        resizeButtons(style, scale);

    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void doDefaultAction(DefaultIntent intent) {
        eventBus.post(new LevelsScreenIntent());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void doBacktAction(BackIntent intent) {
        Gdx.app.exit();
    }

    //---------------------------------------------------------------------
    // Helper methods
    //---------------------------------------------------------------------

    private void resizeLogo(float scale) {
        float logoWidth = 512 * scale;
        float logoHeight = 120 * scale;
        float padding = 16 * scale;
        logoCell.width(logoWidth).height(logoHeight).pad(padding);
    }

    private void resizeButtons(Style style, float scale) {
        Button.ButtonStyle buttonStyle = skin.get(style.toString(), TextButton.TextButtonStyle.class);
        float buttonWidth = 400 * scale;
        float buttonHeight = 85 * scale;
        float padding = 8 * scale;
        // start button
        startButton.setStyle(buttonStyle);
        startButtonCell.width(buttonWidth).height(buttonHeight).pad(padding);
        // options button
        optionsButton.setStyle(buttonStyle);
        optionsButtonCell.width(buttonWidth).height(buttonHeight).pad(padding);
    }

}
