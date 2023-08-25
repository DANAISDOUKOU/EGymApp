package dipl.danai.app.model; 

public enum WorkoutTypes {
	YOGA("Yoga"),
  	TABATA("Tabata"),
	TOTALBODY("Total Body"),
   	PILATESREFORMER("Pilates Reformer"),
  	TABATAUPORDOWN("Tabata Up or Down"),
   	FATBURNING("Fat Burning"),
   	REALRYDER("Real Ryder");
  	/*<option th:value="Pilates Mat">PILATES MAT</option>
   	<option th:value="Fitness Training">FITNESS TRAINING</option>
   	<option th:value="Legs and Abs">LEGS AND ABS</option>
  	<option th:value="Kangoo">KANGOO</option>
   	<option th:value="TRX Jump">TRX JUMPS</option>
   	<option th:value="Zumba">ZUMBA</option>
  	<option th:value="Body Sculpture">BODY SCULPTURE</option>
   	<option th:value="Cross Fit">CROSS FIT</option>
   	<option th:value="One More Round">ONE MORE ROUND</option>
  	<option th:value="Trx Tabata">TRX TABATA</option>
   	<option th:value="Tabata R2">TABATA R2</option>
   	<option th:value="Brazillian Butt">BRAZILLIAN BUTT</option>
  	<option th:value="Jumping Abs">JUMPING ABS</option>
   	<option th:value="Step Aerobic">STEP AEROBIC</option>
   	<option th:value="Boot Camp">BOOT CAMP</option>
  	<option th:value="Kangoo Jumps">KANGOO JUMPS</option>
   	<option th:value="Upper Body">UPPER BODY</option>
   	<option th:value="30 sec More">30 SEC MORE</option>
  	<option th:value="Trx Bar">TRX BAR</option>
   	<option th:value="Cross Training">CROSS TRAINING</option>
   	<option th:value="Tae Bo">TAE BO</option>
  	<option th:value="Core And Cardio">CORE AND CARDIO</option>
   	<option th:value="Insanity">INSANITY</option>
   	<option th:value="Callisthenics Beginners">CALLISTHENICS BEGINNERS</option>
  	<option th:value="Pole Beginners">POLE BEGINNERS</option>
   	<option th:value="Pole Intermediate">POLE INTERMEDIATE</option>
   	<option th:value="Pole Advanced">POLE ADVANCED</option>
  	<option th:value="Hoops Beginners">HOOPS BEGINNERS</option>
   	<option th:value="Hoops Advanced">HOOPS ADVANCED</option>
   	<option th:value="Hoops Intermediate">HOOPS INTERMEDIATEoption>
  	<option th:value="Callisthenics Intermediate">CALLISTHENICS INTERMEDIATE</option>
   	<option th:value="Callisthenics Advanced">CALLI*/
   	
   private final String value;

    private WorkoutTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
