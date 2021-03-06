/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.raffaeleconforti.ilpsolverwrapper.impl.gurobi;

import com.raffaeleconforti.ilpsolverwrapper.ILPSolverExpression;
import com.raffaeleconforti.ilpsolverwrapper.ILPSolverVariable;
import gurobi.GRBLinExpr;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 4/4/17.
 */
public class Gurobi_Expression implements ILPSolverExpression {

    private GRBLinExpr linearExpression;

    public Gurobi_Expression(GRBLinExpr linearExpression) {
        this.linearExpression = linearExpression;
    }

    public GRBLinExpr getLinearExpression() {
        return linearExpression;
    }

    @Override
    public void addTerm(ILPSolverVariable variable, double coefficient) {
        linearExpression.addTerm(coefficient, ((Gurobi_Variable) variable).getVariable());
    }

}
