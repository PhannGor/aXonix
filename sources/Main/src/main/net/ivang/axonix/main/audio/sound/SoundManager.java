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

package net.ivang.axonix.main.audio.sound;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.ivang.axonix.main.actors.game.level.Protagonist;
import net.ivang.axonix.main.actors.game.level.bonuses.Bonus;
import net.ivang.axonix.main.audio.sound.wrappers.CustomSoundWrapper;
import net.ivang.axonix.main.audio.sound.wrappers.SequentialSoundWrapper;
import net.ivang.axonix.main.audio.sound.wrappers.SimpleSoundWrapper;
import net.ivang.axonix.main.audio.sound.wrappers.SoundWrapper;
import net.ivang.axonix.main.events.facts.ButtonClickFact;
import net.ivang.axonix.main.events.facts.EnemyBounceFact;
import net.ivang.axonix.main.events.facts.ObtainedPointsFact;
import net.ivang.axonix.main.events.facts.TailBlockFact;
import net.ivang.axonix.main.events.intents.BackIntent;
import net.ivang.axonix.main.events.intents.DefaultIntent;
import net.ivang.axonix.main.events.intents.SfxVolumeIntent;
import net.ivang.axonix.main.preferences.PreferencesWrapper;

/**
 * @author Ivan Gadzhega
 * @since 0.2
 */
public class SoundManager {

    private float sfxVolume;

    @Inject
    public SoundManager(PreferencesWrapper preferences, EventBus eventBus) {
        this.sfxVolume = preferences.getSfxVolume();
        eventBus.register(this);
        Sounds.initAll();
    }

    //---------------------------------------------------------------------
    // Subscribers
    //---------------------------------------------------------------------

    @Subscribe
    @SuppressWarnings("unused")
    public void onSfxVolumeChange(SfxVolumeIntent intent) {
        sfxVolume = intent.getVolume();
        // play sample sound
        Sounds.ENEMY_BOUNCE.play(sfxVolume);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEnemyBounce(EnemyBounceFact fact) {
        Sounds.ENEMY_BOUNCE.play(sfxVolume);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNewTailBlock(TailBlockFact fact) {
        Sounds.TAIL_BLOCK.play(sfxVolume);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPointsObtained(ObtainedPointsFact fact) {
        int points = fact.getPoints();
        if (points < ObtainedPointsFact.QUANTITY_1) {
            Sounds.FILLING_SHORT_1.play(sfxVolume);
        } else if (points < ObtainedPointsFact.QUANTITY_2){
            Sounds.FILLING_SHORT_2.play(sfxVolume);
        } else if (points < ObtainedPointsFact.QUANTITY_3) {
            Sounds.FILLING_SHORT_3.play(sfxVolume);
        } else {
            Sounds.FILLING.play(sfxVolume);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onBonusObtained(Bonus bonus) {
        Sounds.BONUS.play(sfxVolume);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onProtagonistStateChange(Protagonist.State state) {
        if (state == Protagonist.State.DYING) {
            Sounds.PROT_DYING.play(sfxVolume);
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onButtonClick(ButtonClickFact fact) {
        Sounds.BUTTON_CLICK.play(sfxVolume);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onDefaultIntent(DefaultIntent intent) {
        Sounds.BUTTON_CLICK.play(sfxVolume);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onBackIntent(BackIntent intent) {
        Sounds.BACK_INTENT.play(sfxVolume);
    }

    //---------------------------------------------------------------------
    // Nested Classes
    //---------------------------------------------------------------------

    private enum Sounds {
        ENEMY_BOUNCE("data/audio/sounds/enemy_bounce.ogg", false, 150, 100),
        BUTTON_CLICK("data/audio/sounds/button_click.ogg", false, 100, 0),
        BACK_INTENT("data/audio/sounds/back_intent.ogg"),
        PROT_DYING("data/audio/sounds/prot_dying.ogg"),
        TAIL_BLOCK("data/audio/sounds/tail_block.ogg"),
        BONUS("data/audio/sounds/bonus.ogg"),
        FILLING_SHORT_1("data/audio/sounds/filling_short_1.ogg"),
        FILLING_SHORT_2("data/audio/sounds/filling_short_2.ogg"),
        FILLING_SHORT_3("data/audio/sounds/filling_short_3.ogg"),
        FILLING("data/audio/sounds/filling_1.ogg",
                "data/audio/sounds/filling_2.ogg",
                "data/audio/sounds/filling_3.ogg");

        private final SoundWrapper sound;

        private Sounds(String path) {
            this.sound = new SimpleSoundWrapper(path);
        }

        private Sounds(String path, boolean concurrent, int gapMin, int gapRange) {
            this.sound = new CustomSoundWrapper(path, concurrent, gapMin, gapRange);
        }

        private Sounds(String... paths) {
            this.sound = new SequentialSoundWrapper(paths);
        }

        /**
         * Initializes all sound wrappers for this enum.
         *
         * Should be called outside, because on android static classes may keep on living even though
         * the game has been closed and then music will not be reinitialized properly after reopening.
         */

        public static void initAll() {
            for (Sounds sound : values()) {
                sound.init();
            }
        }

        public void init() {
            sound.init();
        }

        public long play(float volume) {
            return sound.play(volume);
        }
    }
}
