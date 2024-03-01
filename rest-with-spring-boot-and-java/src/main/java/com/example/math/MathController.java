package com.example.math;

import com.example.math.converters.NumberConverter;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.*;

import static com.example.math.converters.NumberConverter.convertToDouble;
import static com.example.math.converters.NumberConverter.isNumeric;

@RestController
public class MathController {
    SimpleMath math = new SimpleMath();
    @RequestMapping(value = "/sum/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double sum(@PathVariable(name = "numberOne") String numberOne,
                      @PathVariable(name = "numberTwo") String numberTwo) throws Exception{
        if(!isNumeric(numberOne) || !isNumeric(numberTwo)){
            throw new UnsupportedOperationException("Please set a numeric value");
        }
        return math.sum(convertToDouble(numberOne), convertToDouble(numberTwo));
    }

    @RequestMapping(value = "/sub/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double sub(@PathVariable(name = "numberOne") String numberOne,
                      @PathVariable(name = "numberTwo") String numberTwo) throws Exception{
        if(!isNumeric(numberOne) || !isNumeric(numberTwo))
            throw new UnsupportedOperationException("Please set a numeric value");

        return math.sub(convertToDouble(numberOne), convertToDouble(numberTwo));
    }

    @RequestMapping(value = "/mult/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double mult(@PathVariable(name = "numberOne") String numberOne,
                       @PathVariable(name = "numberTwo") String numberTwo) throws Exception{
        if(!isNumeric(numberOne) || !isNumeric(numberTwo))
            throw new UnsupportedOperationException("Please set a numeric value");

        return math.mult(convertToDouble(numberOne), convertToDouble(numberTwo));
    }

    @RequestMapping(value = "/div/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double div(@PathVariable(name = "numberOne") String numberOne,
                       @PathVariable(name = "numberTwo") String numberTwo) throws Exception{
        if(!isNumeric(numberOne) || !isNumeric(numberTwo))
            throw new UnsupportedOperationException("Please set a numeric value");

        return math.div(convertToDouble(numberOne), convertToDouble(numberTwo));
    }

    @RequestMapping(value = "/mean/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double mean(@PathVariable(name = "numberOne") String numberOne,
                       @PathVariable(name = "numberTwo") String numberTwo) throws Exception{
        if(!isNumeric(numberOne) || !isNumeric(numberTwo))
            throw new UnsupportedOperationException("Please set a numeric value");

        return math.mean(convertToDouble(numberOne), convertToDouble(numberTwo));
    }

    @RequestMapping(value = "/sqrt/{number}", method = RequestMethod.GET)
    public Double sqrt(@PathVariable(name = "number") String number) throws Exception{
        if(!isNumeric(number))
            throw new UnsupportedOperationException("Please set a numeric value");

        return math.sqrt(convertToDouble(number));
    }

}
