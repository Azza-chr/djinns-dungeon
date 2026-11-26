package tn.designpattern.djinnsdungeon.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/** Charge les poses d'un personnage Kenney (Toon Characters) et les dessine aux pieds. */
public class SpriteSet {
    private static final String BASE =
            "/tn/designpattern/djinnsdungeon/sprites/kenney_toon-characters/";
    private final Map<String, Image> poses = new HashMap<>();
    private final String folder, prefix;
    private double scale = 1;

    public SpriteSet(String folder, String prefix, double targetHeight) {
        this.folder = folder;
        this.prefix = prefix;
        for (int i = 0; i < 8; i++) get("walk" + i);
        for (String p : new String[]{"idle", "attack0", "attack1", "attack2", "hurt", "fallDown", "cheer0", "cheer1"}) {
            get(p);
        }
        Image idle = get("idle");
        if (idle != null && idle.getHeight() > 0) scale = targetHeight / idle.getHeight();
    }

    public boolean isAvailable() { return get("idle") != null; }

    public Image get(String pose) {
        Image img = poses.get(pose);
        if (img == null) {
            img = load(pose);
            if (img != null) poses.put(pose, img);
        }
        return img;
    }

    private Image load(String pose) {
        String path = BASE + folder + "/PNG/Poses HD/character_" + prefix + "_" + pose + ".png";
        try (InputStream in = SpriteSet.class.getResourceAsStream(path)) {
            return in == null ? null : new Image(in);
        } catch (IOException e) {
            return null;
        }
    }

    /** Dessine la pose avec les pieds en (x, groundY). Les sprites regardent vers la droite. */
    public void draw(GraphicsContext g, String pose, double x, double groundY,
                     boolean facingRight, double alpha) {
        Image img = get(pose);
        if (img == null) img = get("idle");
        if (img == null) return;
        double w = img.getWidth() * scale, h = img.getHeight() * scale;
        g.save();
        g.setGlobalAlpha(alpha);
        g.translate(x, groundY);
        if (!facingRight) g.scale(-1, 1);
        g.drawImage(img, -w / 2, -h, w, h);
        g.restore();
    }
}