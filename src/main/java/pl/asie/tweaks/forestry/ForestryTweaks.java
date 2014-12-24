package pl.asie.tweaks.forestry;

import forestry.api.genetics.AlleleManager;
import forestry.apiculture.genetics.BeeMutation;

/**
 * @author Vexatos
 */
public class ForestryTweaks {
	public static final ForestryTweaks INSTANCE = new ForestryTweaks();

	public void addMonasticRecipe() {
		new BeeMutation(
			AlleleManager.alleleRegistry.getAllele("forestry.speciesDiligent"),
			AlleleManager.alleleRegistry.getAllele("forestry.speciesRural"),
			AlleleManager.alleleRegistry.getSpeciesRoot("rootBees").getTemplate("forestry.speciesMonastic"), 10);
	}
}
