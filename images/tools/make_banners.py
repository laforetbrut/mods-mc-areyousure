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

import fx  # noqa: E402
import kit  # noqa: E402
import pixelfont as font  # noqa: E402
from PIL import Image, ImageFilter  # noqa: E402
from theme import Theme  # noqa: E402

SRC = os.path.join(ROOT, "neoforge-1.21.1", "run-capture", "screenshots")
OUT = os.path.dirname(HERE)
THEME = Theme.load(os.path.join(HERE, "theme.json"))
BRAND = os.path.join(ROOT, "Branding", "logo", "png")
# The Just Replay export of images/tools/footage/meadow.json, cut to frames (meadow_0001.png...).
HERO_FRAMES = 136

# Every banner is finished by EffectCraft before it is encoded: grade, vignette, a few cubes.
kit.LOOK = fx.look(THEME, vibrance=14, vignette=22, particles=6, particle_type="cube")

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
    # A frame of the Just Replay dolly past the pig and the oak; the brand logo with its tagline on a dark veil on the left, where the meadow is empty.
    logo = Image.open(os.path.join(BRAND, "are-you-sure-horizontal-tagline-reverse-1024.png")).convert("RGBA")
    logo = logo.resize((380, round(logo.height * 380 / logo.width)), Image.LANCZOS)
    shadow = Image.new("RGBA", logo.size, (0, 0, 0, 0))
    shadow.putalpha(logo.getchannel("A").point(lambda a: a * 0.6))
    shadow = shadow.filter(ImageFilter.GaussianBlur(6))
    veil = Image.new("RGBA", (800, 300), (0, 0, 0, 0))
    for x in range(560):
        a = int(170 * (1 - x / 560) ** 1.6)
        veil.paste((20, 20, 25, a), (x, 0, x + 1, 300))
    ly = (300 - logo.height) // 2
    # A moving camera changes every pixel of every frame and weighs 11 MB here: the GIF keeps one
    # graded frame of the dolly, the motion comes from the light sweep and the particles.
    frames = []
    for n in [1] * 68:
        f = Image.open(src(f"meadow_{n:04d}.png")).convert("RGB").crop((0, 260, 1600, 860)).resize((800, 300), Image.LANCZOS)
        f = f.convert("RGBA")
        f.alpha_composite(veil)
        f.alpha_composite(shadow, (28, ly + 4))
        f.alpha_composite(logo, (24, ly))
        frames.append(kit.frame_border(f.convert("RGB"), THEME))
    look = fx.look(THEME, vibrance=14, vignette=20, particles=6, particle_type="cube", sweep_box=(26, ly, 410, ly + logo.height))
    kit.finish(look(frames), out("hero.gif"), look=False)


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

    # No particles here: they would drift over the text of the screen.
    kit.LOOK = fx.look(THEME, vibrance=14, vignette=22, particles=0)
    kit.clip_scene(out("flow.gif"), THEME, kit.clip(SRC, "flow", FLOW_FIRST, FLOW_LAST), crop=FLOW_CROP, size=(800, 400),
                   plate=src("flow_0008.png"), boxes=[(110, 20, 690, 380)], threshold=6, overlay=overlay)


# ------------------------------------------------------------------ the pass: one captcha, then chain, then ask again

CHAIN_FIRST, CHAIN_LAST = 208, 420


def chain():
    kit.LOOK = fx.look(THEME, vibrance=14, vignette=22, particles=6, particle_type="cube")
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
