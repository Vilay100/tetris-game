# Tetris Game

Created during week 46 2026 during "prao".

Launcher tips, to remove console window:
Change row 4 from:

```
"%DIR%\java" %JLINK_VM_OPTIONS% -m game.tetris/game.tetris.App %*

```

to:

```
start "Tetris Game" "%DIR%\javaw" %JLINK_VM_OPTIONS% -m game.tetris/game.tetris.App %*
```

