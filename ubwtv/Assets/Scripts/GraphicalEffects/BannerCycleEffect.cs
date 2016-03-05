using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Assets.Scripts.GraphicalEffects
{
    class BannerCycleEffect : GraphicalEffect
    {

        public override void PerformChange()
        {
            GetTargetObject().GetComponent<BannerPicker>().setNextBanner();
        }
    }
}
