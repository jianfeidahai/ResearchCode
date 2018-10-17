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

package com.raffaeleconforti.wrappers.settings;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Adriano on 28/06/2017.
 */
public class HM6MiningSettings extends MiningSettings {
    private Set<String> settingLabels;

    public HM6MiningSettings() {
        super();
        settingLabels = new HashSet<>();
        settingLabels.add("dependencyThresholdHM6");
        settingLabels.add("L1lThresholdHM6");
        settingLabels.add("L2lThresholdHM6");
        settingLabels.add("longDepThresholdHM6");
        settingLabels.add("relativeToBestThresholdHM6");
        settingLabels.add("allConnectedHM6");
        settingLabels.add("longDependencyHM6");
    }

    public Set<String> getSettingLabels() { return new HashSet<String>(settingLabels); }
}
