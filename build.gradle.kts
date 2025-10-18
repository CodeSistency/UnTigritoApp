// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // NOTA: Usa los alias EXACTOS que tienes en tu libs.versions.toml
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}