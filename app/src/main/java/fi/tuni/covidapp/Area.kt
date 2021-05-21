package fi.tuni.covidapp

// Class for creating objects for different health care districts in Finland.
class Area(var sidCases: String, var sidVaccine : String, var name: String, var imageId: Int, var population: Int) {
    var cases : String = ""
    var vaccines : String = ""

    fun getVaccinatedPercentage() : String {
        if (vaccines != "") {
            val p : Double = vaccines.toDouble() / population * 100
            return p.toInt().toString()
        }
        return ""
    }
}