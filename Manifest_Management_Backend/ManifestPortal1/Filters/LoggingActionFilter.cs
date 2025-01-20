using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Serilog;

namespace ManifestPortal1.Filters
{
    public class LoggingActionFilter : IAsyncActionFilter
    {
        private readonly ILogger<LoggingActionFilter> _logger;
        public LoggingActionFilter(ILogger<LoggingActionFilter> logger)
        {
            _logger = logger;
        }


        public async Task OnActionExecutionAsync(ActionExecutingContext context, ActionExecutionDelegate next)
        {
            var controllerName = context.ActionDescriptor.RouteValues["controller"];
            var actionName = context.ActionDescriptor.RouteValues["action"];
            var actionArguments = context.ActionArguments;

            _logger.LogInformation("Executing {Controller} - {Action} with arguments {@ActionArguments}",
                controllerName, actionName, actionArguments);

            var stopwatch = System.Diagnostics.Stopwatch.StartNew();

            var executedContext = await next();

            stopwatch.Stop();

            if (executedContext.Exception != null)
            {
                _logger.LogError(executedContext.Exception, "Error occured while executing {Controller} - {Action} with arguments {@ActionArguments}",
                   controllerName, actionName, actionArguments);
            }
            else
            {
                if (executedContext.Result is ObjectResult result)
                {
                    if (result.StatusCode == StatusCodes.Status200OK)
                    {
                        _logger.LogInformation("Executed {Controller} - {Action} successfully with result: {@Result}. Duration: {Duration} ms.",
                            controllerName, actionName, result.Value, stopwatch.ElapsedMilliseconds);
                    }
                    else if (result.StatusCode == StatusCodes.Status400BadRequest)
                    {
                        _logger.LogWarning("Bad Request at {Controller} - {Action} with result: {@Result}. Duration: {Duration} ms.",
                            controllerName, actionName, result.Value, stopwatch.ElapsedMilliseconds);
                    }
                    else if (result.StatusCode == StatusCodes.Status404NotFound)
                    {
                        _logger.LogWarning("Not Found at {Controller} - {Action} with result: {@Result}. Duration: {Duration} ms.",
                            controllerName, actionName, result.Value, stopwatch.ElapsedMilliseconds);
                    }
                    else if (result.StatusCode >= StatusCodes.Status500InternalServerError)
                    {
                        _logger.LogError("Server Error at {Controller} - {Action} with result: {@Result}. Duration: {Duration} ms.",
                            controllerName, actionName, result.Value, stopwatch.ElapsedMilliseconds);
                    }
                }
                else
                {
                    _logger.LogInformation("Executed {Controller} - {Action} with result type: {ResultType}. Duration: {Duration} ms.",
                        controllerName, actionName, executedContext.Result.GetType().Name, stopwatch.ElapsedMilliseconds);
                }
            }
        }
    }
}


