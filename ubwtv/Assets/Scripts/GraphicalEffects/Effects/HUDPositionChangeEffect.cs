using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using UnityEngine;

namespace Assets.GraphicalEffects.Effects
{
    class HUDPositionChangeEffect : ContinuousGraphicalEffect
    {
        public Vector2 endPosition;

        public Vector2 startPosition;

        public bool customStartPosition;
            
        protected override void Cleanup()
        {

        }

        protected override void PerformContinuousChange()
        {
            RectTransform transform = GetTargetObject().GetComponent<RectTransform>();
            transform.anchoredPosition = Vector2.Lerp(startPosition, endPosition, currentStatus);
        }

        protected override void Prepare()
        {
            if (customStartPosition)
            {

            }
            else
            {
                startPosition = GetTargetObject().GetComponent<RectTransform>().anchoredPosition;
            }
        }
    }
}
