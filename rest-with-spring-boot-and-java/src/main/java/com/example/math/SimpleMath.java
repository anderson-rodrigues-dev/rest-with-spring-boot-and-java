package com.example.math;

import com.example.math.converters.NumberConverter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.example.math.converters.NumberConverter.convertToDouble;
import static com.example.math.converters.NumberConverter.isNumeric;

public class SimpleMath {
    public Double sum(Double numberOne,
                      Double numberTwo) throws Exception{

        return numberOne + numberTwo;
    }
    public Double sub(Double numberOne,
                     Double numberTwo) throws Exception{
        return numberOne - numberTwo;
    }

    public Double mult(Double numberOne,
                       Double numberTwo) throws Exception{
        return numberOne * numberTwo;
    }

    public Double div(Double numberOne,
                      Double numberTwo) throws Exception{
        return numberOne / numberTwo;
    }

    public Double mean(Double numberOne,
                       Double numberTwo) throws Exception{

        return (numberOne + numberTwo) / 2;
    }

    public Double sqrt(Double number) throws Exception{
        return Math.sqrt(number);
    }
}
