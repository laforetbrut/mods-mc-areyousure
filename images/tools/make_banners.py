"""Builds the animated banners of the project page (images/*.gif) from footage recorded in game.

    python images/tools/make_banners.py [names...]

Footage: images/tools/capture/scenes.txt, recorded on NeoForge 1.21.1 into
neoforge-1.21.1/run-capture/screenshots. Frame numbers below come from the `mark` lines of its log.

Author: vyrriox
"""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(os.path.dirname(HERE))
sys.path.insert(0, HERE)

import kit  # noqa: E402
import pixelfont as font  # noqa: E402
from theme import Theme  # noqa: E402

SRC = os.path.join(ROOT, "neoforge-1.21.1", "run-capture", "screenshots")
OUT = os.path.dirname(HERE)
THEME = Theme.load(os.path.join(HERE, "theme.json"))

# Marks logged by the capture script (frame numbers of the "flow" clip).
YES = 59
WRONG = 111
SOLVE = 166
DONE = 201
HITS = [223, 244, 265, 286]
TURN = 313
OAK = 351


def src(name):
    return os.path.join(SRC, name)


def out(name):
    return os.path.join(OUT, name)


# ------------------------------------------------------------------ the banner on top of the page

def hero():
    # The pig and the oak on the right, the name on the left. A long name: its first words go to the kicker.
    # The official logo of the mod (the one shown on CurseForge) is the emblem.
    kit.hero_banner(out("hero.gif"), THEME, src("hero_plate.png"), "SURE?", kicker="ARE YOU",
                    logo=os.path.join(HERE, "art", "logo.png"), crop=(0, 150, 1600, 750), side="left")


# ------------------------------------------------------------------ the screen: ask, confirm, fail, solve

FLOW_FIRST, FLOW_LAST = 8, 206
FLOW_CROP = (400, 250, 1200, 650)          # 1:1 with the recording, so the pixel font stays sharp


def flow():
    ptr = kit.pointer(THEME, 2)
    yes_at = (272, 324)                    # the "Yes" button, in banner pixels

    def overlay(canvas, i, n):
        f = i + FLOW_FIRST
        if YES - 30 <= f <= YES + 12:
            t = kit.span(f, YES - 30, YES - 4)
            x = int(560 + (yes_at[0] - 560) * t)
            y = int(380 + (yes_at[1] - 380) * t)
            kit.paste(canvas, ptr, x, y)
            if f >= YES:
                kit.click_ring(canvas, yes_at[0], yes_at[1], f - YES, THEME.accent)

    kit.clip_scene(out("flow.gif"), THEME, kit.clip(SRC, "flow", FLOW_FIRST, FLOW_LAST), crop=FLOW_CROP, size=(800, 400),
                   plate=src("flow_0008.png"), boxes=[(110, 20, 690, 380)], threshold=6, overlay=overlay)


# ------------------------------------------------------------------ the pass: one captcha, then chain, then ask again

CHAIN_FIRST, CHAIN_LAST = 208, 420


def chain():
    def overlay(canvas, i, n):
        f = i + CHAIN_FIRST
        hits = sum(1 for h in HITS if f >= h + 2)
        if f < TURN + 10:
            text = f"1 CAPTCHA  >  {hits} HIT{'S' if hits > 1 else ''}" if hits else "1 CAPTCHA SOLVED"
            chip = kit.label(text, THEME, scale=2, icon_name="check")
        else:
            chip = kit.label("NEW ACTION  >  ASKED AGAIN", THEME, scale=2, icon_name="warning")
        kit.paste(canvas, chip, 400, 290, anchor="c")

    kit.clip_scene(out("chain.gif"), THEME, kit.clip(SRC, "flow", CHAIN_FIRST, CHAIN_LAST), crop=(0, 130, 1600, 770), size=(800, 320),
                   plate=src(f"flow_{CHAIN_FIRST:04d}.png"), boxes=[(-40, -40, 840, 360)], threshold=10, overlay=overlay)


BUILDERS = {f.__name__: f for f in (hero, flow, chain)}


def main():
    names = sys.argv[1:] or list(BUILDERS)
    for name in names:
        BUILDERS[name]()


if __name__ == "__main__":
    main()
