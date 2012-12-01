package net.ivang.xonix.main;

import com.badlogic.gdx.InputAdapter;
import net.ivang.xonix.main.GameScreen.State;

import static com.badlogic.gdx.Input.Keys;

/**
 * @author Ivan Gadzhega
 * @since 0.1
 */
public class GameScreenInputProcessor extends InputAdapter {

    private XonixGame game;
    private GameScreen gameScreen;

    public  GameScreenInputProcessor(XonixGame game, GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.SPACE:
            case Keys.MENU:  // TODO: Change it to some gesture
//                gameScreen.getNotification().setVisible(false);
                switch (gameScreen.getState()) {
                    case PLAYING:
                        gameScreen.setState(State.PAUSED);
                        break;
                    case PAUSED:
                        gameScreen.setState(State.PLAYING);
                        break;
                    case GAME_OVER:
                        game.setStartScreen();
                        break;
                    case LEVEL_COMPLETED:
                        int nextIndex = gameScreen.getLevelIndex() + 1;
                        if (nextIndex < game.getLevelsFiles().size()) {
                            gameScreen.setLevel(nextIndex);
                        } else {
                            gameScreen.setState(State.WIN);
                        }
                        break;
                    case WIN:
                        game.setStartScreen();
                        break;
                }
                return true;
            case Keys.ESCAPE:
                //TODO: only for testing
                game.setStartScreen();
                return true;
        }
        return false;
    }
}