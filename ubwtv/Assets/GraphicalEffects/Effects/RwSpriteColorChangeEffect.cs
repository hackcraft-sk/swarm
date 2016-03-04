using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;
using UnityEngine.UI;

namespace Assets.GraphicalEffects.Effects
{
    class RwSpriteColorChangeEffect : ContinuousGraphicalEffect
    {
        public Color endColor;

        public Color startColor;

        public bool customStartColor;

        protected override void Cleanup()
        {

        }

        protected override void PerformContinuousChange()
        {
            RawImage image = GetTargetObject().GetComponent<RawImage>();
            image.color = Color.Lerp(startColor, endColor, currentStatus);
        }

        protected override void Prepare()
        {
            if (!customStartColor)
            {
                RawImage image = GetTargetObject().GetComponent<RawImage>();
                startColor = image.color;
            }
        }
    }
}
