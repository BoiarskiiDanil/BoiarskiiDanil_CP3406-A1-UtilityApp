package au.edu.jcu.assessment.utilityapp

enum class SingaporeRegion(
    val displayName: String,
    val apiKey: String
) {
    NORTH("North", "north"),
    SOUTH("South", "south"),
    EAST("East", "east"),
    WEST("West", "west"),
    CENTRAL("Central", "central")
}