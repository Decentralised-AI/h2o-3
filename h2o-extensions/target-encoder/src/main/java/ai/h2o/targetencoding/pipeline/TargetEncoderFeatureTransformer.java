package ai.h2o.targetencoding.pipeline;

import ai.h2o.targetencoding.TargetEncoderModel;
import ai.h2o.targetencoding.TargetEncoderModel.TargetEncoderParameters;
import hex.Model;
import hex.pipeline.ModelAsFeatureTransformer;
import hex.pipeline.PipelineContext;
import water.fvec.Frame;

import static ai.h2o.targetencoding.TargetEncoderModel.DataLeakageHandlingStrategy.KFold;

public class TargetEncoderFeatureTransformer extends ModelAsFeatureTransformer<TargetEncoderFeatureTransformer, TargetEncoderModel, TargetEncoderParameters> {

  public TargetEncoderFeatureTransformer(TargetEncoderParameters params) {
    super(params);
  }

  public TargetEncoderFeatureTransformer(TargetEncoderParameters params, String modelId) {
    super(params, modelId);
  }

  @Override
  public boolean isCVSensitive() {
    return _params._data_leakage_handling == KFold;
  }

  @Override
  protected void doPrepare(PipelineContext context) {
    if (context != null) {
      if (isCVSensitive() && context._params._fold_column != null) {
        _params._fold_column = context._params._fold_column;
      }
    }
    super.doPrepare(context);
  }
  
  @Override
  protected Frame doTransform(Frame fr, FrameType type, PipelineContext context) {
    assert type != null;
    assert context != null || type == FrameType.Scoring;
    validateTransform();
    switch (type) {
      case Training:
        if (useFoldTransform(context._params)) {
          return getModel().transformTraining(fr, context._params._cv_fold);
        } else {
          return getModel().transformTraining(fr);
        }
      case Validation:
        if (useFoldTransform(context._params)) {
          return getModel().transformTraining(fr);
        } else {
          return getModel().transform(fr);
        }
      case Scoring:
      default:
        return getModel().transform(fr);
    }
  }

  private boolean useFoldTransform(Model.Parameters params) {
    return isCVSensitive() && params._cv_fold >= 0;
  }
  
}
