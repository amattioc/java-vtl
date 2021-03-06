package no.ssb.vtl.script.functions;

/*
 * -
 *  * ========================LICENSE_START=================================
 * * Java VTL
 *  *
 * %%
 * Copyright (C) 2017 Arild Johan Takvam-Borge
 *  *
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 *
 */

import no.ssb.vtl.model.VTLNumber;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class AggregationAvgFunctionTest {

    @Test
    public void testIntegers() {
        int number1 = 10;
        int number2 = 20;

        List<VTLNumber> numbers = Lists.newArrayList(
                VTLNumber.of(number1),
                VTLNumber.of(number2)
        );
        VTLNumber<?> result = new AggregationAvgFunction().apply(numbers);
        assertThat(result).isEqualTo(VTLNumber.of((double) (number1 + number2) / numbers.size()));
    }

    @Test
    public void testAvgDoubles() {
        double number1 = 5.5;
        double number2 = 10.25;

        List<VTLNumber> numbers = Lists.newArrayList(
                VTLNumber.of(number1),
                VTLNumber.of(number2)
        );
        VTLNumber<?> result = new AggregationAvgFunction().apply(numbers);
        assertThat(result).isEqualTo(VTLNumber.of((number1 + number2) / numbers.size()));
    }

    @Test
    public void testAvgIntegerAndDouble() {
        double number1 = 5.5;
        int number2 = 10;

        List<VTLNumber> numbers = Lists.newArrayList(
                VTLNumber.of(number1),
                VTLNumber.of(number2)
        );
        VTLNumber<?> result = new AggregationAvgFunction().apply(numbers);
        assertThat(result).isEqualTo(VTLNumber.of((number1 + number2) / numbers.size()));
    }

    @Test
    public void testAvgIntegerWithNull() {
        int number1 = 5;
        int number2 = 10;

        List<VTLNumber> numbers = Lists.newArrayList(
                VTLNumber.of(number1),
                VTLNumber.of(number2),
                null,
                VTLNumber.of((Double)null)
        );
        VTLNumber<?> result = new AggregationAvgFunction().apply(numbers);
        assertThat(result).isEqualTo(VTLNumber.of((double) (number1 + number2) / (numbers.size() - 2)));
    }

    @Test
    public void testAvgDoubleWithNull() {
        double number1 = 5.5;
        double number2 = 10.25;

        List<VTLNumber> numbers = Lists.newArrayList(
                VTLNumber.of(number1),
                VTLNumber.of(number2),
                VTLNumber.of((Double)null),
                null
        );
        VTLNumber<?> result = new AggregationAvgFunction().apply(numbers);
        assertThat(result).isEqualTo(VTLNumber.of((number1 + number2) / (numbers.size() - 2)));
    }

}
