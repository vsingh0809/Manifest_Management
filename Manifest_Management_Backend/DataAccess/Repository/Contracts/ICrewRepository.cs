using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ManifestPortal1.Entities.DTO;
using ManifestPortal1.Entities.Models;

namespace ManifestPortal1.DataAccess.Repository.Contracts
{
    public interface ICrewRepository
    {
        IEnumerable<Crewoperator> GetAllOperators();
        Crewoperator GetOperatorById(int id);
        Crewoperator UpdateOperator(Crewoperator operatorEntity);
        //Crewoperator AddOperator(Crewoperator operatorEntity);
        Task<Crewoperator> RegisterCrewOperator(Crewoperator Crewoperator);


        public Task<Crewoperator> GetOperatorByCrewoperatorUserName(string userName);
        public Crewoperator GetOperatorByCrewoperatorEmail(string email);
        Crewoperator GetCrewOperatorByUserName(string userName);
        Task<Crewoperator> GetOperatorByCrewoperatorEmailAsync(string userEmail);
        Task<Crewoperator?> GetCrewOperatorByIdAsync(int crewId);
        Task UpdateCrewOperatorAsync(Crewoperator crewOperator);


    }
}
