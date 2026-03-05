package co.edu.escuelaing.reflexionlab.demo;

import co.edu.escuelaing.reflexionlab.annotations.GetMapping;
import co.edu.escuelaing.reflexionlab.annotations.RequestParam;
import co.edu.escuelaing.reflexionlab.annotations.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class MathController {

    @GetMapping("/pi")
    public String pi() {
        return String.valueOf(Math.PI);
    }

    @GetMapping("/square")
    public String square(@RequestParam(value = "n", defaultValue = "0") String n) {
        int number = Integer.parseInt(n);
        return String.valueOf(number * number);
    }

    @GetMapping("/time")
    public String time() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
