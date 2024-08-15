package com.ducksteam.needleseye.entity.effect;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.ducksteam.needleseye.Main;

import java.util.concurrent.ConcurrentHashMap;

import static com.ducksteam.needleseye.Main.particleSystem;

public class DamageEffectManager {
	public static ConcurrentHashMap<ParticleEffect, Long> times = new ConcurrentHashMap<>(); // stores the expiration times of each effect

	private static final String staticEffectAddress = "models/effects/bleed.pfx"; // file path to the effect
	private static ParticleEffect staticEffect; // the original copy of the effect

	private static final Matrix4 tmpMat = new Matrix4();

	public static void loadStaticEffect(){ // load the effect from the asset manager
		staticEffect = Main.assMan.get(getStaticEffectAddress());
	}

	public static void create(Vector3 position) {
		// generate copy of effect
		ParticleEffect tmpEffect = staticEffect.copy();
		tmpEffect.setBatch(particleSystem.getBatches());
		tmpEffect.setTransform(tmpMat.setToTranslation(position));
		tmpEffect.init();
		tmpEffect.start();

		// add effect to world
		times.put(tmpEffect, Main.getTime() + 300);
		particleSystem.add(tmpEffect);
	}

	public static void update(){
		times.forEach((ParticleEffect effect, Long time) -> {
			if (Main.getTime() > time) {
				times.remove(effect);
				particleSystem.remove(effect);
			}
		});
	}

	public static String getStaticEffectAddress() {
		return staticEffectAddress;
	}
}