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

package com.raffaeleconforti.kernelestimation.distribution.kernel.impl;

import com.raffaeleconforti.kernelestimation.distribution.kernel.Kernel;

/**
 * Created by conforti on 29/01/2016.
 */
public class SilvermanKernel implements Kernel {

    @Override
    public double getKernel(double u) {
        return (0.5 * Math.exp(-(Math.abs(u) / Math.sqrt(2))) * Math.sin((Math.abs(u) / Math.sqrt(2)) + (Math.PI / 4)));
    }

    @Override
    public double value(double v) {
        return getKernel(v);
    }
}
