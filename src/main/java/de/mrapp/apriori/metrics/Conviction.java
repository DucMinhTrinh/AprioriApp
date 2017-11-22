/*
 * Copyright 2017 Michael Rapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.mrapp.apriori.metrics;

import de.mrapp.apriori.AssociationRule;
import de.mrapp.apriori.Metric;
import org.jetbrains.annotations.NotNull;

import static de.mrapp.util.Condition.ensureNotNull;

/**
 * A metric, which measures the conviction of an association rule. By definition, conviction is the
 * ratio of the expected frequency that a rule makes an incorrect prediction, if body and head were
 * independent, over the frequency of incorrect predictions. For example, a conviction of 1.2
 * states, that the items, which are contained in the body and head of a rule, occur together 1.2
 * times as often as if the association between head and body was purely random.
 *
 * @author Michael Rapp
 * @since 1.2.0
 */
public class Conviction implements Metric {

    @Override
    public final double evaluate(@NotNull final AssociationRule rule) {
        ensureNotNull(rule, "The rule may not be null");
        double numerator = 1 - rule.getHead().getSupport();
        double denominator = 1 - new Confidence().evaluate(rule);
        return denominator == 0 ? 0 : numerator / denominator;
    }

    @Override
    public final double minValue() {
        return 0;
    }

    @Override
    public final double maxValue() {
        return Double.MAX_VALUE;
    }

}