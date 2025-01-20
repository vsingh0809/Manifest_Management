using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using ManifestPortal1.DataAccess.Repository.Contracts;
using ManifestPortal1.Entities.Models;
using ManifestPortal1.Entities.EFCore;

namespace ManifestPortal1.DataAccess.Repository.Classes
{
    public class CrewoperatorRepo : ICrewRepository
    {
        private readonly TeamAContext dbContext;

        public CrewoperatorRepo(TeamAContext dbContext)
        {
            this.dbContext = dbContext;
        }

        public IEnumerable<Crewoperator> GetAllOperators()
        {
            return dbContext.Crewoperators.ToList();
        }

        public Crewoperator GetOperatorById(int id)
        {
            return dbContext.Crewoperators.Find(id);
        }

        public Crewoperator UpdateOperator(Crewoperator operatorEntity)
        {
            dbContext.Crewoperators.Update(operatorEntity);
            dbContext.SaveChanges();
            return operatorEntity;
        }



        // Updated method signature to Task<Crewoperator> to support async operations
        public async Task<Crewoperator> RegisterCrewOperator(Crewoperator operatorEntity)
        {
            await dbContext.Crewoperators.AddAsync(operatorEntity); //  Properly await AddAsync
            await dbContext.SaveChangesAsync(); //  Use SaveChangesAsync
            return operatorEntity;
        }


        //-----------------------------------------
        //finding Crewoperator by usrname 
        public async Task<Crewoperator> GetOperatorByCrewoperatorUserName(string userName)
        {
            return await dbContext.Crewoperators.FirstOrDefaultAsync(u => u.CrewUsername == userName);

        }



        // finding Crewoperator by usernName----
        public Crewoperator GetCrewOperatorByUserName(string userName) { return dbContext.Crewoperators.FirstOrDefault(u => u.CrewUsername == userName); }


        //----------------------------------------------
        //finding Crewoperator by usrname or email
        public Crewoperator GetOperatorByCrewoperatorEmail(string userEmail)
        {
            return dbContext.Crewoperators.FirstOrDefault(u => u.Email == userEmail);


        }



        //finding Crewoperator by usrname or email Async method
        public async Task<Crewoperator> GetOperatorByCrewoperatorEmailAsync(string userEmail)
        {
            try
            {
                return await dbContext.Crewoperators.FirstOrDefaultAsync(u => u.Email == userEmail);
            }
            catch (Exception ex)
            {
                // Log and rethrow a custom exception (if needed)
                throw new Exception("Error occurred while fetching operator details", ex);
            }
        }

        public async Task<Crewoperator?> GetCrewOperatorByIdAsync(int crewId)
        {
            return await dbContext.Crewoperators.FindAsync(crewId);
        }

        public async Task UpdateCrewOperatorAsync(Crewoperator crewOperator)
        {
            dbContext.Crewoperators.Update(crewOperator);
            await dbContext.SaveChangesAsync();
        }
    }
}
